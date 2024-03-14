package com.pako2k.banknotescatalog.data.stats

import kotlinx.serialization.Serializable


@Serializable
data class CurrencyStats (
    val id : UInt,
    val name : String,
    val numSeries: Int,
    val numDenominations: Int,
    val numNotes: Int,
    val numVariants: Int,
    val price: Float? = null
)