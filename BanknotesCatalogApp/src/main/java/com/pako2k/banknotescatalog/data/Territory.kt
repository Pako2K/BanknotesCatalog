package com.pako2k.banknotescatalog.data

import androidx.compose.ui.graphics.ImageBitmap
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

    private var isExtended = false

    var flag : ImageBitmap? = null
        private set
    var parentExt : Territory? = null
        private set
    var successorsExt : List<Territory> = listOf()
        private set

    var predecessorsExt : List<Territory> = listOf()
        private set

    // Extend with additional data this territory
    fun extend(
       territoriesList: List<Territory>,
       flags: Map<String,ImageBitmap>
    ) {
        if (isExtended) return

        flag = flags[this.flagName]
        if (this.parentId != null){
            parentExt = territoriesList.find { it.id == this.parentId }
            if (parentExt != null) parentExt!!.flag = flags[parentExt!!.flagName]
        }

        val predecessorsMutableList = mutableListOf<Territory>()
        val successorsMutableList = mutableListOf<Territory>()
        territoriesList.forEach{
            if (it.successors?.find { it2 -> this.id == it2.id } != null) {
                predecessorsMutableList.add(it)
                it.flag = flags[it.flagName]
            }

            if (this.successors?.find{ it3 -> it.id == it3.id} != null  ){
                successorsMutableList.add(it)
                it.flag = flags[it.flagName]
            }
        }

        successorsExt = successorsMutableList
        predecessorsExt = predecessorsMutableList

        isExtended = true
    }

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
        if (isExtended != other.isExtended) return false
        if (flag != other.flag) return false
        if (parentExt != other.parentExt) return false
        if (successorsExt != other.successorsExt) return false
        if (predecessorsExt != other.predecessorsExt) return false

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
        result = 31 * result + isExtended.hashCode()
        result = 31 * result + (flag?.hashCode() ?: 0)
        result = 31 * result + (parentExt?.hashCode() ?: 0)
        result = 31 * result + successorsExt.hashCode()
        result = 31 * result + predecessorsExt.hashCode()
        return result
    }
}
