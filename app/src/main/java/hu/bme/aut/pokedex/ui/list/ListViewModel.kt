package hu.bme.aut.pokedex.ui.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.bme.aut.pokedex.model.ui.Poke
import hu.bme.aut.pokedex.repo.FirebaseRepository
import hu.bme.aut.pokedex.repo.PokeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val ITEMS_PER_PAGE = 20

@HiltViewModel
class ListViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val firebaseRepository: FirebaseRepository,
    private val pokeRepository: PokeRepository
) : ViewModel() {



    val items: Flow<PagingData<Poke>> = Pager(
        config = PagingConfig(pageSize = ITEMS_PER_PAGE, enablePlaceholders = false),
        pagingSourceFactory = { pokeRepository.pokePagingSource() }
    )
        .flow
        /*.map {
            pagingData ->
            pagingData.filter {
                poke -> poke.name!!.contains("pika")
            }
        }*/
        .cachedIn(viewModelScope)

}