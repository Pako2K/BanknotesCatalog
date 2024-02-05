package com.pako2k.banknotescatalog.data

import kotlinx.serialization.Serializable

@Serializable
data class TerritoryId (
    val id : UInt
)

@Serializable
data class TerritoryKey (
    val id : UInt,
    val name : String
)


@Serializable
data class Territory (
    val id : UInt,
    val name : String,
    val iso3 : String? = null,
    val continent : Continent,
    val territoryType : TerritoryTypeKey,
    val iso2 : String? = null,
    val officialName : String,
    val start : Int,
    val end : Int? = null,
    val parentId : UInt? = null,
    val successors : Array<TerritoryId>? = null,
    val description : String? = null,
    val uri: String
) {
    val flagName : String = iso3?.lowercase()?:name.lowercase().replace(",", "").replace(" ", "")
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Territory

        if (id != other.id) return false
        if (name != other.name) return false
        if (iso3 != other.iso3) return false
        if (continent != other.continent) return false
        if (territoryType != other.territoryType) return false
        if (iso2 != other.iso2) return false
        if (officialName != other.officialName) return false
        if (start != other.start) return false
        if (end != other.end) return false
        if (parentId != other.parentId) return false
        if (successors != null) {
            if (other.successors == null) return false
            if (!successors.contentEquals(other.successors)) return false
        } else if (other.successors != null) return false
        if (description != other.description) return false
        if (uri != other.uri) return false
        if (flagName != other.flagName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (iso3?.hashCode() ?: 0)
        result = 31 * result + continent.hashCode()
        result = 31 * result + territoryType.hashCode()
        result = 31 * result + (iso2?.hashCode() ?: 0)
        result = 31 * result + officialName.hashCode()
        result = 31 * result + start
        result = 31 * result + (end ?: 0)
        result = 31 * result + (parentId?.hashCode() ?: 0)
        result = 31 * result + (successors?.contentHashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + uri.hashCode()
        result = 31 * result + flagName.hashCode()
        return result
    }

}
