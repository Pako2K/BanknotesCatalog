package com.pako2k.banknotescatalog.app

import com.pako2k.banknotescatalog.data.Continent
import com.pako2k.banknotescatalog.data.Currency
import com.pako2k.banknotescatalog.data.Territory
import com.pako2k.banknotescatalog.data.TerritoryType

data class MainUiData (
    var continents : List<Continent> = listOf(),
    var territoryTypes : List<TerritoryType> = listOf(),
    var territories : List<Territory> = listOf(),
    var currencies : List<Currency> = listOf()
)