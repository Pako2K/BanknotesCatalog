package com.pako2k.banknotescatalog.data

import kotlinx.serialization.Serializable

@Serializable
data class Continent (
    val id : UInt,
    val name : String
)
