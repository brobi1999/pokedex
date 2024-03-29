package hu.bme.aut.pokedex.ui.list

import androidx.lifecycle.*
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.bme.aut.pokedex.model.domain.PokeResult
import hu.bme.aut.pokedex.model.ui.Poke
import hu.bme.aut.pokedex.repo.FirebaseRepository
import hu.bme.aut.pokedex.repo.PokeRepository
import hu.bme.aut.pokedex.util.MyUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val ITEMS_PER_PAGE = 20

@HiltViewModel
class ListViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val firebaseRepository: FirebaseRepository,
    private val pokeRepository: PokeRepository
) : ViewModel() {

    private lateinit var pokeList: List<PokeResult>

    private lateinit var favList: MutableList<String>

    var shouldDisplayFavouritesOnly: Boolean = false

    init {
        getCache()
    }

    private suspend fun getAllPokemon() {
        pokeList = pokeRepository.getPokemonList(offset = 0, loadSize = 10000).results
    }

    private suspend fun getAllFavPokNames() {
        favList = firebaseRepository.getFavouritePokeNamesForCurrentUser() as MutableList<String>
    }

    private val _isError = MutableLiveData<Exception?>(null)
    val isError: LiveData<Exception?>
        get() = _isError

    fun errorReceived(){
        _isError.value = null
    }

    private fun getCache(){
        viewModelScope.launch {
            try{
                listOf(
                    launch { getAllPokemon() },
                    launch { getAllFavPokNames() }
                ).joinAll()
                _isCacheReady.value = true
            }
            catch (e: Exception){
                e.printStackTrace()
                _isError.value = e
            }

        }
    }

    private val _isCacheReady = MutableLiveData(false)
    val isCacheReady: LiveData<Boolean>
        get() = _isCacheReady

    var nameQuery: String = ""

    var filterFire: Boolean = false
    var filterGrass: Boolean = false
    var filterElectric: Boolean = false

    private fun shouldIncludePokemon(poke: Poke): Boolean{
        return if(poke.typeSlotTwo != null){
            MyUtil.shouldIncludeType(poke.typeSlotOne.toString(), filterFire, filterGrass, filterElectric)
        } else{
            MyUtil.shouldIncludeType(poke.typeSlotOne.toString(), poke.typeSlotTwo.toString(), filterFire, filterGrass, filterElectric)
        }

    }

    val items: Flow<PagingData<Poke>> = Pager(
        config = PagingConfig(pageSize = ITEMS_PER_PAGE, enablePlaceholders = false),
        pagingSourceFactory = {
            pokeRepository.pokePagingSource(pokeList, nameQuery, favList, shouldDisplayFavouritesOnly)
        }
    )
        .flow
        .map {
            pagingData ->
            pagingData.filter {
                poke -> shouldIncludePokemon(poke)
            }
        }
        .cachedIn(viewModelScope)

    private val _favAdded = MutableLiveData<String?>(null)
    val favAdded: LiveData<String?>
        get() = _favAdded

    private val _favRemoved = MutableLiveData<String?>(null)
    val favRemoved: LiveData<String?>
        get() = _favRemoved

    fun addPokeToFavourites(name: String){
        viewModelScope.launch {
            firebaseRepository.addPokeNameToFavourites(name)
            _favAdded.value = name
        }
    }

    fun removePokeFromFavourites(name: String) {
        viewModelScope.launch {
            firebaseRepository.removePokeNameFromFavourites(name)
            _favRemoved.value = name
        }
    }

    private val _refresh = MutableLiveData<Boolean>(false)
    val refresh: LiveData<Boolean>
        get() = _refresh

    fun refreshReceived(){
        _refresh.value = false
    }

    fun refreshView() {
        viewModelScope.launch {
            //update favList
            getAllFavPokNames()
            _refresh.value = true
        }
    }

}