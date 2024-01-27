package com.pako2k.banknotescatalog.data

import android.content.Context
import androidx.compose.ui.graphics.ImageBitmap
import com.pako2k.banknotescatalog.localsource.FlagsLocalDataSource
import com.pako2k.banknotescatalog.network.BanknotesAPIClient
import com.pako2k.banknotescatalog.network.BanknotesNetworkDataSource
import com.pako2k.banknotescatalog.ui.parts.Sorting


class BanknotesCatalogRepository private constructor(
    private val flagsLocalDataSource: FlagsLocalDataSource,
    private val banknotesNetworkDataSource: BanknotesNetworkDataSource
){
    private var flags : Map<String,ImageBitmap> = mapOf()

    var continents : Map<UInt, Continent> = mapOf()
        private set
    var territoryTypes : Map<UInt, TerritoryType> = mapOf()
        private set
    var territories : List<Territory> = listOf()
        private set

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
    }


    fun sortTerritories(sortBy : Territory.SortableCol , sortingDir : Sorting){
        // Sort data
        territories = when (sortBy){
            Territory.SortableCol.NAME -> if (sortingDir == Sorting.DESC) territories.sortedByDescending { it.name  } else territories.sortedBy { it.name  }
            Territory.SortableCol.START -> if (sortingDir == Sorting.DESC) territories.sortedByDescending { it.start } else territories.sortedBy { it.start }
            Territory.SortableCol.END -> if (sortingDir == Sorting.DESC) territories.sortedByDescending { it.end } else territories.sortedBy { it.end }
        }
    }

    fun getTerritoriesData() : List<Map<String, Any?>> {
        val tmp  = mutableListOf<Map<String, Any?>>()

        for (ter in territories){
            tmp.add(territoryToMap(ter))
        }

        return tmp
    }

    fun getTerritoriesDataByContinent (byContinent : UInt?) : List<Map<String, Any?>> {
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

}
