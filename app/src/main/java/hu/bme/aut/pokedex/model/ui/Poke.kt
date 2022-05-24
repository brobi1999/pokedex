package hu.bme.aut.pokedex.model.ui

data class Poke (
    val id: Int? = null,
    val name: String? = null,
    val hp: Int? = null,
    val atk: Int? = null,
    val def: Int? = null,
    val sp: Int? = null,
    val back_default: String? = null,
    val back_female: String? = null,
    val front_default: String? = null,
    val front_female: String? = null,
    val typeSlotOne: String? = null
)