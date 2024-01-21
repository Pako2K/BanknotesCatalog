package com.pako2k.banknotescatalog.data

import kotlinx.serialization.Serializable

@Serializable
data class Territory (
    val id : UInt,
    val name : String,
    val iso3 : String? = null,
    val continentId : UInt,
    val territoryTypeId : UInt,
    val start : Int,
    val end : Int? = null,
    val uri: String
)

fun territoryToMap(territory : Territory) : MutableMap<String, String> {
    return mutableMapOf(
        "id" to territory.id.toString(),
        "name" to territory.name,
        "iso3" to (territory.iso3 ?: ""),
        "continentId" to territory.continentId.toString(),
        "territoryTypeId" to territory.territoryTypeId.toString(),
        "start" to territory.start.toString(),
        "end" to (if (territory.end == null) "" else territory.end.toString()),
        "uri" to territory.uri)
}