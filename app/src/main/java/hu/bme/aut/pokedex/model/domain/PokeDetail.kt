package hu.bme.aut.pokedex.model.domain

import com.google.gson.annotations.SerializedName

data class PokeDetail (
    @SerializedName("stats") val stats: List<ComplexStat>? = null,
    @SerializedName("sprites") val sprites: SpriteValues? = null,
    @SerializedName("types") val types: List<DomainType>? = null
)

data class Stat (
    @SerializedName("name") val name : String? = null,
)

data class ComplexStat(
    @SerializedName("base_stat") val base_stat: Int? = null,
    @SerializedName("stat") val stat: Stat? = null
)

data class SpriteValues(
    @SerializedName("back_default") val back_default: String? = null,
    @SerializedName("back_female") val back_female: String? = null,
    @SerializedName("front_default") val front_default: String? = null,
    @SerializedName("front_female") val front_female: String? = null
)

data class DomainType (
    @SerializedName("slot") val slot : Int? = null,
    @SerializedName("type") val type : DomainTypeDetail? = null,
)

data class DomainTypeDetail (
    @SerializedName("name") val name : String? = null
)