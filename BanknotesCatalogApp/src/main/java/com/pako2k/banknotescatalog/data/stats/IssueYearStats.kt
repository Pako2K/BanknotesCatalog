package com.pako2k.banknotescatalog.data.stats

import kotlinx.serialization.Serializable


@Serializable
data class  IssueYearTotalStats(
    val numTerritories: Int,
    val numCurrencies: Int,
    val numSeries: Int,
    val numDenominations: Int,
    val numNotes: Int,
    val numVariants: Int,
)

@Serializable
data class IssueYearContinentStats (
    val id : UInt,
    val numTerritories: Int,
    val numCurrencies: Int,
    val numSeries: Int,
    val numDenominations: Int,
    val numNotes: Int,
    val numVariants: Int,
)

@Serializable
data class IssueYearStats (
    val issueYear : Int,
    val continentStats: Array<IssueYearContinentStats>,
    val totalStats: IssueYearTotalStats,
    val price: Float? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IssueYearStats

        if (issueYear != other.issueYear) return false
        if (!continentStats.contentEquals(other.continentStats)) return false
        if (totalStats != other.totalStats) return false
        return price == other.price
    }

    override fun hashCode(): Int {
        var result = issueYear
        result = 31 * result + continentStats.contentHashCode()
        result = 31 * result + totalStats.hashCode()
        result = 31 * result + (price?.hashCode() ?: 0)
        return result
    }
}