package hu.bme.aut.pokedex.repo

import hu.bme.aut.pokedex.model.domain.PokeList
import retrofit2.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PokeRepository @Inject constructor(){

    private val pageSize = 20
    private val pokeApi = RetrofitHelper.getInstance().create(PokeApi::class.java)


    suspend fun getPokemonList(pageNum: Int): PokeList {
        return pokeApi.getPokeList(pageSize, pageNum*pageSize).await()
    }

}