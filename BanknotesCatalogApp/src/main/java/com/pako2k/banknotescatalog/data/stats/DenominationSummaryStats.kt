package com.pako2k.banknotescatalog.data.stats


class DenominationSummaryStats(
    // Current denomination = for existing currencies ( w/o an endDate)
    val current : Data,
    // Extinct denomination = for extinct currencies
    val extinct : Data
){
    data class Data(
        val total : Int,
        val collection : Int = 0
    )

    val total = current + extinct
}

operator fun DenominationSummaryStats.Data.plus(other : DenominationSummaryStats.Data) =
    DenominationSummaryStats.Data(
        this.total + other.total,
        this.collection + other.collection
    )

operator fun DenominationSummaryStats.plus(other : DenominationSummaryStats) = DenominationSummaryStats(
    this.current + other.current,
    this.extinct + other.extinct,
)