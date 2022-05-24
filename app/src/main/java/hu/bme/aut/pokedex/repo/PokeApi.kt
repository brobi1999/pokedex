package hu.bme.aut.pokedex.repo

import hu.bme.aut.pokedex.model.domain.PokeList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PokeApi{

    @GET("/pokemon")
    suspend fun getPokeList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
    ) : Call<PokeList>
}