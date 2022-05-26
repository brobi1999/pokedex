package hu.bme.aut.pokedex.ui.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.bme.aut.pokedex.model.domain.PokeResult
import hu.bme.aut.pokedex.model.ui.Poke
import hu.bme.aut.pokedex.repo.FirebaseRepository
import hu.bme.aut.pokedex.repo.PokeRepository
import hu.bme.aut.pokedex.util.MyUtil
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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

    fun getAllPokemon(): Job {
        val job = viewModelScope.launch {
            pokeList = pokeRepository.getPokemonList(offset = 0, loadSize = 10000).results
        }
        return job
    }



    fun isCacheEmpty(): Boolean{
        return pokeList.isEmpty()
    }

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
        pagingSourceFactory = { pokeRepository.pokePagingSource(pokeList, nameQuery) }
    )
        .flow
        .map {
            pagingData ->
            pagingData.filter {
                poke -> shouldIncludePokemon(poke)
            }
        }
        .cachedIn(viewModelScope)

}