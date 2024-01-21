package com.pako2k.banknotescatalog.data

import kotlinx.serialization.Serializable

enum class CurrencyType{
    OWNED,
    SHARED
}

@Serializable
data class Currency(
    val id : UInt,
    val name : String,
    val territory : UInt,
    val type: CurrencyType
)
