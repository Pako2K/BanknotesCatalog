package com.pako2k.banknotescatalog.data

import kotlinx.serialization.Serializable


enum class TerritoryTypeEnum(val value : String){
    Ind ("Independent State"),
    T("Territory"),
    NR("Not Recognized State"),
    EU("Economic Union")
}

@Serializable
data class TerritoryTypeKey (
    val id : UInt,
    val name : String
)

@Serializable
data class TerritoryType (
    val id : UInt,
    val name : String,
    val abbreviation : String,
    val description : String
)
