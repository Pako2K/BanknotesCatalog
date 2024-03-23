package com.pako2k.banknotescatalog.data

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable


enum class Grades (
    val title : String, val color : Color, val background : Color, val description : String
){
    UNC ("Uncirculated", Color.Black, Color(0xFF008000), ""),
    XF("Extremely Fine", Color.Black, Color(0xFFadff2f), ""),
    VF("Very Fine", Color.Black, Color(0xFFffff00),""),
    F("Fine", Color.Black, Color(0xFFffa500),""),
    VG("Very Good", Color.White, Color(0xFF785000),""),
    G("Good", Color.White, Color(0xFF5a5000),""),
    P("Poor", Color.White, Color(0xFF3c3200),""),
    REP("Reproduction", Color.Black, Color(0xFF707070),""),
}

@Serializable
data class Item(
    val id : UInt,
    val variantId : UInt,
    val grade : String,
    val quantity : Int,
    val price : Float,
    val seller : String?,
    val purchaseDate : String?,
    val description : String?
)

@Serializable
data class ItemLinked(
    val item : Item,
    val continent : Continent,
    val territory: TerritoryKey,
    val currency: CurrencyKey,
    val denomination : Double,
    val catalogId : String
)