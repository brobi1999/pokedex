package hu.bme.aut.pokedex.repo

import hu.bme.aut.pokedex.model.domain.PokeDetail
import hu.bme.aut.pokedex.model.domain.PokeList
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApi{

    @GET("/api/v2/pokemon/")
    suspend fun getPokeList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
    ) : Response<PokeList>

    @GET("/api/v2/pokemon/{name}")
    suspend fun getPokeDetail(
        @Path("name") name: String
    ) : Response<PokeDetail>
}