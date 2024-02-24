package com.pako2k.banknotescatalog.data

class CurrencySummaryStats(
    // Current territory = Territory w/o an endDate
    val current : Data,
    // Current territory = Territory with an endDate
    val extinct : Data
){
    data class Data(
        val total : Int,
        val collection : Int = 0
    )

    val total = current + extinct
}

operator fun CurrencySummaryStats.Data.plus(other : CurrencySummaryStats.Data) = CurrencySummaryStats.Data(
    this.total + other.total,
    this.collection + other.collection
)

operator fun CurrencySummaryStats.plus(other : CurrencySummaryStats) = CurrencySummaryStats(
    this.current + other.current,
    this.extinct + other.extinct,
)