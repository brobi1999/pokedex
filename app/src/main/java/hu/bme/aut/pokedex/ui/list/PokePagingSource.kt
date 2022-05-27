package hu.bme.aut.pokedex.ui.list
import androidx.paging.PagingSource
import androidx.paging.PagingState
import hu.bme.aut.pokedex.model.domain.PokeResult
import hu.bme.aut.pokedex.model.ui.Poke
import hu.bme.aut.pokedex.repo.PokeRepository
import java.lang.Integer.max
import java.lang.Integer.min

const val STARTING_KEY = 0

class PokePagingSource(
    private val pokeRepository: PokeRepository,
    pokeList: List<PokeResult>,
    private val nameQuery: String,
    private val favList: MutableList<String>,
    private val shouldDisplayFavouritesOnly: Boolean
) :
    PagingSource<Int, Poke>() {

    private var pokeNameList: List<String>

    init {
        pokeNameList = pokeList.map { pokeResult -> pokeResult.name.toString() }
        if(nameQuery.isNotEmpty()){
            pokeNameList = pokeNameList.filter {
                it.contains(nameQuery)
            }
        }
        if(shouldDisplayFavouritesOnly){
            pokeNameList = pokeNameList.filter {
                favList.contains(it)
            }
        }
    }

    private fun ensureValidKey(key: Int) = max(STARTING_KEY, key)

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Poke> {
        val start = params.key ?: STARTING_KEY
        val range = start.until(start + params.loadSize)
        var queryList = mutableListOf<String>()
        queryList = try{
            pokeNameList.subList(start, min(range.last, pokeNameList.size)) as MutableList<String>
        } catch (e:Exception){
            e.printStackTrace()
            pokeNameList as MutableList<String>
        }

        return try {
            val response = pokeRepository.getPokemonDetails(queryList)
            response.map {
                it.isFavourite = favList.contains(it.name)
            }

            LoadResult.Page(
                data = response,
                prevKey = when (start) {
                    STARTING_KEY -> null
                    else -> ensureValidKey(key = range.first - params.loadSize)
                },
                nextKey = when(range.last >= pokeNameList.size){
                    true -> null
                    false -> range.last
                }
            )

        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }

    // The refresh key is used for the initial load of the next PagingSource, after invalidation
    override fun getRefreshKey(state: PagingState<Int, Poke>): Int? {
        // In our case we grab the item closest to the anchor position
        // then return its id - (state.config.pageSize / 2) as a buffer
        /*val anchorPosition = state.anchorPosition ?: return null
        val poke = state.closestItemToPosition(anchorPosition) ?: return null
        val closestItemIndex = pokeNameList.indexOf(poke.name)
        return ensureValidKey(key = closestItemIndex - (state.config.pageSize / 2))*/
        return null
    }
}