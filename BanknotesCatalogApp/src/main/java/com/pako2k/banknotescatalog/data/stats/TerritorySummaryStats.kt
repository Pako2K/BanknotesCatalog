package com.pako2k.banknotescatalog.data.stats

data class TerritorySummaryStats(
    // Current territory = Territory w/o an endDate
    val current : Data,
    // Current territory = Territory with an endDate
    val extinct : Data
){
    data class Data(
        val total : Int,
        // Issuer territory = Territory which owns a Currency
        val issuer: Int,
        val collection : Int = 0
    )

    val total = current + extinct
}
operator fun TerritorySummaryStats.Data.plus(other : TerritorySummaryStats.Data) = TerritorySummaryStats.Data(
    this.total + other.total,
    this.issuer + other.issuer,
    this.collection + other.collection
)

operator fun TerritorySummaryStats.plus(other : TerritorySummaryStats) = TerritorySummaryStats(
    this.current + other.current,
    this.extinct + other.extinct,
)