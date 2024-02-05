package com.pako2k.banknotescatalog.data

import com.pako2k.banknotescatalog.R
import kotlinx.serialization.Serializable

@Serializable
data class Continent (
    val id : UInt,
    val name : String
)


val ContinentIconMap : Map<String,Int> = mapOf(
    "Africa" to R.drawable.africa,
    "North America" to R.drawable.northamerica,
    "South America" to R.drawable.southamerica,
    "Asia" to R.drawable.asia,
    "Europe" to R.drawable.europe,
    "Oceania" to R.drawable.oceania
)