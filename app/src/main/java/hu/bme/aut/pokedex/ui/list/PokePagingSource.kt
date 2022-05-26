package hu.bme.aut.pokedex.ui.list
import androidx.paging.PagingSource
import androidx.paging.PagingState
import hu.bme.aut.pokedex.model.ui.Poke
import hu.bme.aut.pokedex.repo.PokeRepository
import java.lang.Integer.max

const val STARTING_OFFSET = 0

class PokePagingSource(private val pokeRepository: PokeRepository) :
    PagingSource<Int, Poke>() {

    private fun ensureValidKey(key: Int) = max(STARTING_OFFSET, key)

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Poke> {
        val start = params.key ?: STARTING_OFFSET
        val range = start.until(start + params.loadSize)
        return try {
            val response = pokeRepository.getUiPokeListWithDetail(offset = start, loadSize = params.loadSize)

            LoadResult.Page(
                data = response,
                prevKey = when (start) {
                    STARTING_OFFSET -> null
                    else -> ensureValidKey(key = range.first - params.loadSize)
                },
                nextKey = range.last + 1
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
        val anchorPosition = state.anchorPosition ?: return null
        val poke = state.closestItemToPosition(anchorPosition) ?: return null
        return ensureValidKey(key = poke.id - 1 - (state.config.pageSize / 2))
    }
}