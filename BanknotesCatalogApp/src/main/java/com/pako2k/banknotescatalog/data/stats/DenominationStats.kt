package com.pako2k.banknotescatalog.data.stats

import kotlinx.serialization.Serializable

@Serializable
data class  DenomTotalStats(
    val isCurrent: Boolean,
    val numTerritories: Int,
    val numCurrencies: Int,
    val numNotes: Int,
    val numVariants: Int
)

@Serializable
data class DenomContinentStats (
    val id : UInt,
    val isCurrent: Boolean,
    val numTerritories: Int,
    val numCurrencies: Int,
    val numNotes: Int,
    val numVariants: Int,
)
@Serializable
data class DenominationStats (
    val denomination : Double,
    val continentStats : Array<DenomContinentStats>,
    val totalStats : DenomTotalStats,
    val price: Float? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DenominationStats

        if (denomination != other.denomination) return false
        if (!continentStats.contentEquals(other.continentStats)) return false
        if (totalStats != other.totalStats) return false
        return price == other.price
    }

    override fun hashCode(): Int {
        var result = denomination.hashCode()
        result = 31 * result + continentStats.contentHashCode()
        result = 31 * result + totalStats.hashCode()
        result = 31 * result + (price?.hashCode() ?: 0)
        return result
    }
}