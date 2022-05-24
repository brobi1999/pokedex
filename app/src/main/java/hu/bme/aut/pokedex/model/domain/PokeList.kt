package hu.bme.aut.pokedex.model.domain

data class PokeList (
    val count: Long? = null,
    val next: String? = null,
    val previous: String? = null,
    val results: List<PokeResult>? = null
)

data class PokeResult (
    val name: String? = null,
    val url: String? = null
)


