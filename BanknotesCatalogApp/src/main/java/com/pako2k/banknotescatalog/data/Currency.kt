package com.pako2k.banknotescatalog.data

import kotlinx.serialization.Serializable


@Serializable
data class TerritoryLink(
    val territory: TerritoryKey,
    val start : String,
    val end : String? = null
)

@Serializable
data class SuccessorCurrency (
    val id : UInt,
    val rate: Float? = null
)

@Serializable
data class CurrencyUnit(
    val id : UInt,
    val name : String,
    val namePlural : String? = null,
    val abbreviation : String? = null,
    val value : Float? = null
)

@Serializable
data class Currency (
    val id : UInt,
    val name : String,
    val namePlural : String? = null,
    val fullName : String,
    val symbol : String? = null,
    val iso3 : String? = null,
    val continent : Continent,
    val start : String,
    val end : String? = null,
    val ownedBy : Array<TerritoryLink>,
    val sharedBy : Array<TerritoryLink>? = null,
    val usedBy : Array<TerritoryLink>? = null,
    val successor : SuccessorCurrency? = null,
    val units : Array<CurrencyUnit>? = null,
    val description : String? = null,
    val uri: String,
) {
    val startYear = strDateToYear(start) as Int
    val endYear = strDateToYear(end)


    private fun strDateToYear(date : String?) : Int? {
        return date?.substring(0, minOf(4, date.length))?.toInt()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Currency

        if (id != other.id) return false
        if (name != other.name) return false
        if (namePlural != other.namePlural) return false
        if (fullName != other.fullName) return false
        if (symbol != other.symbol) return false
        if (iso3 != other.iso3) return false
        if (continent != other.continent) return false
        if (start != other.start) return false
        if (end != other.end) return false
        if (!ownedBy.contentEquals(other.ownedBy)) return false
        if (sharedBy != null) {
            if (other.sharedBy == null) return false
            if (!sharedBy.contentEquals(other.sharedBy)) return false
        } else if (other.sharedBy != null) return false
        if (usedBy != null) {
            if (other.usedBy == null) return false
            if (!usedBy.contentEquals(other.usedBy)) return false
        } else if (other.usedBy != null) return false
        if (successor != other.successor) return false
        if (units != null) {
            if (other.units == null) return false
            if (!units.contentEquals(other.units)) return false
        } else if (other.units != null) return false
        if (description != other.description) return false
        if (uri != other.uri) return false
        if (startYear != other.startYear) return false
        if (endYear != other.endYear) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (namePlural?.hashCode() ?: 0)
        result = 31 * result + fullName.hashCode()
        result = 31 * result + (symbol?.hashCode() ?: 0)
        result = 31 * result + (iso3?.hashCode() ?: 0)
        result = 31 * result + continent.hashCode()
        result = 31 * result + start.hashCode()
        result = 31 * result + (end?.hashCode() ?: 0)
        result = 31 * result + ownedBy.contentHashCode()
        result = 31 * result + (sharedBy?.contentHashCode() ?: 0)
        result = 31 * result + (usedBy?.contentHashCode() ?: 0)
        result = 31 * result + (successor?.hashCode() ?: 0)
        result = 31 * result + (units?.contentHashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + uri.hashCode()
        result = 31 * result + startYear
        result = 31 * result + (endYear ?: 0)
        return result
    }

}
