package hu.bme.aut.pokedex.repo

import android.util.Log
import hu.bme.aut.pokedex.model.domain.PokeDetail
import hu.bme.aut.pokedex.model.domain.PokeList
import hu.bme.aut.pokedex.model.ui.Poke
import hu.bme.aut.pokedex.ui.list.PokePagingSource
import retrofit2.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PokeRepository @Inject constructor(){

    private val pokeApi = RetrofitHelper.getInstance().create(PokeApi::class.java)

    fun pokePagingSource(): PokePagingSource = PokePagingSource(this)

    private suspend fun getPokemonList(offset: Int, loadSize: Int): PokeList {
        val response = pokeApi.getPokeList(loadSize, offset)
        if(response.isSuccessful)
            return response.body()!!
        else
            throw RuntimeException("Retrofit call failed! " + response.raw())
    }

    private suspend fun getPokemonListWithDetail(offset: Int, loadSize: Int): List<PokeDetail>{
        val list = mutableListOf<PokeDetail>()
        val pokeList = getPokemonList(offset, loadSize)
        if(pokeList.results == null )
            return list
        for(pokeResult in pokeList.results){
            val pokeDetail = pokeResult.name?.let { getPokemonDetail(it) }
            pokeDetail?.let { list.add(it) }
        }
        return list
    }

    private suspend fun getPokemonDetail(name: String): PokeDetail {
        val response = pokeApi.getPokeDetail(name)
        if(response.isSuccessful)
            return response.body()!!
        else
            throw RuntimeException("Retrofit call failed! " + response.raw())
    }

    suspend fun getUiPokeListWithDetail(offset: Int, loadSize: Int): List<Poke> {
        val domainResults = getPokemonListWithDetail(offset, loadSize)
        val list = mutableListOf<Poke>()
        for(result in domainResults){
            var hp = 0
            var attack = 0
            var defense = 0
            var spAttack = 0

            if(result.stats != null){
                for (stat in result.stats){
                    val value = stat.base_stat
                    if(value != null){
                        when(stat.stat?.name){
                            "hp"-> hp = value
                            "attack" -> attack = value
                            "defense" -> defense = value
                            "special-attack" -> spAttack = value
                        }
                    }

                }
            }

            val poke = Poke(
                id = result.id,
                name = result.name,
                hp = hp,
                atk = attack,
                def = defense,
                sp = spAttack,
                back_default = result.sprites?.back_default,
                back_female = result.sprites?.back_female,
                front_default = result.sprites?.front_default,
                front_female = result.sprites?.front_female,
                typeSlotOne = result.types?.get(0)?.type?.name
            )

            list.add(poke)
        }
        return list
    }

}