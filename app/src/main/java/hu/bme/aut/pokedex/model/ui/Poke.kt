package hu.bme.aut.pokedex.model.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Poke (
    val id: Int,
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
) : Parcelable