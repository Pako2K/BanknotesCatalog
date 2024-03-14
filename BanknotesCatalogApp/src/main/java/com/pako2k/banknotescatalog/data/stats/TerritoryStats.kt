package com.pako2k.banknotescatalog.data.stats

import kotlinx.serialization.Serializable


@Serializable
data class TerritoryStats (
    val id : UInt,
    val name : String,
    val numCurrencies: Int,
    val numSeries: Int,
    val numDenominations: Int,
    val numNotes: Int,
    val numVariants: Int,
    val price: Float? = null
)