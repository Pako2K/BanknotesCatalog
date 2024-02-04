package com.pako2k.banknotescatalog.data

import android.content.Context
import androidx.compose.ui.graphics.ImageBitmap
import com.pako2k.banknotescatalog.localsource.FlagsLocalDataSource
import com.pako2k.banknotescatalog.network.BanknotesAPIClient
import com.pako2k.banknotescatalog.network.BanknotesNetworkDataSource


enum class SortDirection {
    ASC,
    DESC
}

sealed class SortableField

sealed class CurrencySortableField : SortableField()
object CurrencyFieldName : CurrencySortableField()
object CurrencyFieldOwnedBy : CurrencySortableField()
object CurrencyFieldStart : CurrencySortableField()
object CurrencyFieldEnd : CurrencySortableField()


sealed class TerritorySortableField : SortableField()
object TerritoryFieldName : TerritorySortableField()
object TerritoryFieldStart : TerritorySortableField()
object TerritoryFieldEnd : TerritorySortableField()




class BanknotesCatalogRepository private constructor(
    private val flagsLocalDataSource: FlagsLocalDataSource,
    private val banknotesNetworkDataSource: BanknotesNetworkDataSource
){
    private var flags : Map<String,ImageBitmap> = mapOf()

    var continents : Map<UInt, Continent> = mapOf()
        private set
    var territoryTypes : Map<UInt, TerritoryType> = mapOf()
        private set


    // Value set when territories list change
    var territoriesIndexMap : Map<UInt,Int> = mapOf()
        private set
    var territories : List<Territory> = listOf()
        private set(value){
            field = value
            var index = 0
            territoriesIndexMap = field.associate { cur ->
                cur.id to index++
            }
        }

    // Value set when currencies list change
    var currenciesIndexMap : Map<UInt,Int> = mapOf()
        private set
    var currencies : List<Currency> = listOf()
        private set(value){
            field = value
            var index = 0
            currenciesIndexMap = field.associate { cur ->
                cur.id to index++
            }
        }

    companion object {
        private var _repository : BanknotesCatalogRepository? = null

        fun create(ctx : Context, banknotesApiClient : BanknotesAPIClient) : BanknotesCatalogRepository {
            if (_repository==null)
                _repository = BanknotesCatalogRepository(FlagsLocalDataSource(ctx.assets), BanknotesNetworkDataSource(banknotesApiClient))

            return _repository as BanknotesCatalogRepository
        }
    }


    suspend fun fetchFlags() {
        flags = flagsLocalDataSource.getFlags()
    }

    suspend fun fetchContinents() {
        continents = banknotesNetworkDataSource.getContinents().associateBy { cont -> cont.id }
    }

    suspend fun fetchTerritoryTypes() {
        territoryTypes = banknotesNetworkDataSource.getTerritoryTypes().associateBy { type -> type.id }
    }

    // Use after TerritoryTypes and Continents are fetched
    suspend fun fetchTerritories() {
        territories = banknotesNetworkDataSource.getTerritories().filter { ter ->
            continents[ter.continentId]  != null
        }
        var index = 0
        territoriesIndexMap = territories.associate { ter ->
            ter.id to index++
        }
    }
    suspend fun fetchCurrencies() {
        currencies = banknotesNetworkDataSource.getCurrencies()
    }


    fun sortTerritories(sortBy : TerritorySortableField, sortingDir : SortDirection){
        // Sort data
        territories = when (sortBy){
            TerritoryFieldName -> if (sortingDir == SortDirection.DESC) territories.sortedByDescending { it.name  } else territories.sortedBy { it.name  }
            TerritoryFieldStart -> if (sortingDir == SortDirection.DESC) territories.sortedByDescending { it.start } else territories.sortedBy { it.start }
            TerritoryFieldEnd -> if (sortingDir == SortDirection.DESC) territories.sortedByDescending { it.end } else territories.sortedBy { it.end }
        }
    }

    fun sortCurrencies(sortBy : CurrencySortableField, sortingDir : SortDirection){
        // Sort data
        currencies = when (sortBy){
            CurrencyFieldName ->
                if (sortingDir == SortDirection.DESC) currencies.sortedByDescending { it.name } else currencies.sortedBy { it.name }
            CurrencyFieldOwnedBy ->
                if (sortingDir == SortDirection.DESC) currencies.sortedByDescending { it.ownedBy[0].id  } else currencies.sortedBy { it.ownedBy[0].id }
            CurrencyFieldStart ->
                if (sortingDir == SortDirection.DESC) currencies.sortedByDescending { it.startYear } else currencies.sortedBy { it.startYear }
            CurrencyFieldEnd ->
                if (sortingDir == SortDirection.DESC) currencies.sortedByDescending { it.endYear } else currencies.sortedBy { it.endYear }
        }
    }


    fun getTerritoriesData (byContinent : UInt?) : List<Map<String, Any?>> {
        val tmp  = mutableListOf<Map<String, Any?>>()

        if (byContinent == null)
            for (ter in territories){
                tmp.add(territoryToMap(ter))
            }
        else
            for (ter in territories){
                if (ter.continentId == byContinent)
                    tmp.add(territoryToMap(ter))
            }

        return tmp
    }

    /*
    fun getTerritoriesDataByType (byTerritoryType: UInt?) : List<Map<String, Any?>> {
        val tmp  = mutableListOf<Map<String, Any?>>()

        if (byTerritoryType == null)
            for (ter in territories){
                tmp.add(territoryToMap(ter))
            }
        else
            for (ter in territories){
                if (ter.territoryTypeId == byTerritoryType)
                    tmp.add(territoryToMap(ter))
            }

        return tmp
    }

    fun getTerritoriesDataByStart (
        fromStart : Int?,
        toStart : Int?
    ) : List<Map<String, Any?>> {
        val tmp  = mutableListOf<Map<String, Any?>>()

        for (ter in territories){
            if (((fromStart == null) || ( ter.start >= fromStart ))
                && ((toStart == null) || (ter.start <= toStart))
            ){
                tmp.add(territoryToMap(ter))
            }
        }
        return tmp
    }

    fun getTerritoriesDataByEnd (
        fromEnd : Int?,
        toEnd : Int?
    ) : List<Map<String, Any?>> {
        val tmp  = mutableListOf<Map<String, Any?>>()

        for (ter in territories){
            if (((fromEnd == null) || (ter.end != null && ter.end >= fromEnd))
                && ((toEnd == null) || (ter.end != null && ter.end <= toEnd))
            ){
                tmp.add(territoryToMap(ter))
            }
        }
        return tmp
    }

     */

    fun getCurrenciesData (byContinent : UInt?) : List<Map<String, Any?>> {
        val tmp  = mutableListOf<Map<String, Any?>>()

        if (byContinent == null)
            for (cur in currencies){
                tmp.add(currencyToMap(cur))
            }
        else
            for (cur in currencies){
                if (cur.continentId == byContinent)
                    tmp.add(currencyToMap(cur))
            }

        return tmp
    }


    private fun territoryToMap(territory : Territory) : Map<String, Any?>{
        return mapOf(
            "id" to territory.id,
            "iso3" to territory.iso3,
            "flag" to flags[territory.flagName],
            "name" to territory.name,
            "type" to territoryTypes[territory.territoryTypeId]!!.abbreviation,
            "start" to territory.start,
            "end" to territory.end
        )
    }

    private fun currencyToMap(currency : Currency) : Map<String, Any?>{
        return mapOf(
            "id" to currency.id,
            "iso3" to currency.iso3,
            "name" to currency.name,
            "fullName" to currency.fullName,
            "ownedBy" to currency.ownedBy,
            "start" to currency.start,
            "end" to currency.end
        )
    }
}
