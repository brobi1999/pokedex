package hu.bme.aut.pokedex.repo

import hu.bme.aut.pokedex.model.domain.PokeDetail
import hu.bme.aut.pokedex.model.domain.PokeList
import hu.bme.aut.pokedex.model.domain.PokeResult
import hu.bme.aut.pokedex.model.ui.Poke
import hu.bme.aut.pokedex.ui.list.PokePagingSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PokeRepository @Inject constructor(){

    private val pokeApi = RetrofitHelper.getInstance().create(PokeApi::class.java)

    fun pokePagingSource(
        pokeList: List<PokeResult>,
        nameQuery: String,
        favList: MutableList<String>,
        shouldDisplayFavouritesOnly: Boolean,
    ): PokePagingSource = PokePagingSource(this, pokeList, nameQuery, favList, shouldDisplayFavouritesOnly)

    suspend fun getPokemonList(offset: Int, loadSize: Int): PokeList {
        val response = pokeApi.getPokeList(loadSize, offset)
        if(response.isSuccessful)
            return response.body()!!
        else
            throw RuntimeException("Retrofit call failed! " + response.raw())
    }

    private fun convertDomainPokeDetailToUiPoke(pokeDetail: PokeDetail): Poke {
        var hp = 0
        var attack = 0
        var defense = 0
        var spAttack = 0

        if (pokeDetail.stats != null) {
            for (stat in pokeDetail.stats) {
                val value = stat.base_stat
                if (value != null) {
                    when (stat.stat?.name) {
                        "hp" -> hp = value
                        "attack" -> attack = value
                        "defense" -> defense = value
                        "special-attack" -> spAttack = value
                    }
                }

            }
        }



        return Poke(
            id = pokeDetail.id,
            name = pokeDetail.name,
            hp = hp,
            atk = attack,
            def = defense,
            sp = spAttack,
            back_default = pokeDetail.sprites?.back_default,
            back_female = pokeDetail.sprites?.back_female,
            front_default = pokeDetail.sprites?.front_default,
            front_female = pokeDetail.sprites?.front_female,
            typeSlotOne = pokeDetail.types?.get(0)?.type?.name,
            typeSlotTwo = if(pokeDetail.types?.size == 2) pokeDetail.types[1].type?.name else null
        )
    }

    suspend fun getPokemonDetails(nameList: List<String>): List<Poke>{
        val list = mutableListOf<Poke>()
        val domainList = getDomainPokeDetail(nameList)
        for(pokeDetail in domainList){
            list.add(convertDomainPokeDetailToUiPoke(pokeDetail))
        }
        return list
    }

    private suspend fun getDomainPokeDetail(nameList: List<String>): List<PokeDetail>{
        val list = mutableListOf<PokeDetail>()
        for(name in nameList){
            val response = pokeApi.getPokeDetail(name)
            if(response.isSuccessful){
                val pokeDetail = response.body()!!
                list.add(pokeDetail)
            }
            else
                throw RuntimeException("Retrofit call failed! " + response.raw())
        }
        return list
    }

}