package com.pako2k.banknotescatalog.data

import kotlinx.serialization.Serializable

@Serializable
data class Territory (
    val id : UInt,
    val name : String,
    val iso3 : String? = null,
    val continentId : UInt,
    val territoryTypeId : UInt,
    val start : Int,
    val end : Int? = null,
    val uri: String,
) {
    val flagName : String = iso3?.lowercase()?:name.lowercase().replace(",", "").replace(" ", "")
    enum class SortableCol {
        NAME,
        START,
        END
    }
}
