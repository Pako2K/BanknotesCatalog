package com.pako2k.banknotescatalog.data

import kotlinx.serialization.Serializable


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
