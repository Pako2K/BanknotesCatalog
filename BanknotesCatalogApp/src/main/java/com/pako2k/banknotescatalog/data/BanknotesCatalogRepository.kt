package com.pako2k.banknotescatalog.data

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import com.pako2k.banknotescatalog.app.StatsSubColumn
import com.pako2k.banknotescatalog.localsource.FlagsLocalDataSource
import com.pako2k.banknotescatalog.network.BanknotesAPIClient
import com.pako2k.banknotescatalog.network.BanknotesNetworkDataSource
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


enum class SortDirection {
    ASC,
    DESC
}

sealed class SortableField

// Enumeration of Currency fields which can be used for sorting. Implemented as child of SortableField!
sealed class CurrencySortableField : SortableField()
object CurrencyFieldName : CurrencySortableField()
object CurrencyFieldOwnedBy : CurrencySortableField()
object CurrencyFieldStart : CurrencySortableField()
object CurrencyFieldEnd : CurrencySortableField()
object CurrencyFieldIssues : CurrencySortableField()
object CurrencyFieldDenominations : CurrencySortableField()
object CurrencyFieldNotes : CurrencySortableField()
object CurrencyFieldVariants : CurrencySortableField()
object CurrencyFieldPrice : CurrencySortableField()


// Enumeration of Territory fields which can be used for sorting. Implemented as child of SortableField!
sealed class TerritorySortableField : SortableField()
object TerritoryFieldName : TerritorySortableField()
object TerritoryFieldStart : TerritorySortableField()
object TerritoryFieldEnd : TerritorySortableField()
object TerritoryFieldCurrencies : TerritorySortableField()
object TerritoryFieldIssues : TerritorySortableField()
object TerritoryFieldDenominations : TerritorySortableField()
object TerritoryFieldNotes : TerritorySortableField()
object TerritoryFieldVariants : TerritorySortableField()
object TerritoryFieldPrice : TerritorySortableField()




// SINGLETON REPOSITORY OF BANKNOTES CATALOG DATA
//****************************************************
// Public attributes:
//    - flags => Map<String,ImageBitmap>  (READ ONLY)
//    - continents => Map<contId, Continent>  (READ ONLY)
//    - territoryTypes => Map<typeId, TerritoryType> (READ ONLY)
//    - territories => List<Territory> (variable sorted List) (READ ONLY)
//    - currencies => List<Currency> (variable sorted List) (READ ONLY)
//
// Public methods:
//      - BanknotesCatalogRepository.create()
//      - fetch...() => get data from DataSource and store it
//      - sortTerritories(TerritorySortableField, SortDirection) => sort territories
//      - sortCurrencies(CurrencySortableField, SortDirection) => sort currencies
//      - getTerritories(contId) : List<Territory> => return list of filtered territories
//      - getCurrenciesData(contId) : List<Map<String, Any?>> => return list of filtered territories as a Map(fieldName, fieldValue)

class BanknotesCatalogRepository private constructor(
    private val flagsLocalDataSource: FlagsLocalDataSource,
    private val banknotesNetworkDataSource: BanknotesNetworkDataSource,
    private val continentCacheRepository: ContinentCacheRepository,
    private val terTypeCacheRepository: TerritoryTypeCacheRepository
){
    var flags : Map<String,ImageBitmap> = mapOf()
        private set

    var continents : Map<UInt, Continent> = mapOf()
        private set

    var territoryTypes : Map<UInt, TerritoryType> = mapOf()
        private set

    // Value set when territories list is sorted
    var territories : List<Territory> = listOf()
        private set

    var territoryCatStats : List<TerritoryStats> = listOf()
        private set

    var territoryColStats : List<TerritoryStats> = listOf()
        private set

    // Value set when currencies list is sorted
    var currencies : List<Currency> = listOf()
        private set

    var currencyCatStats : List<CurrencyStats> = listOf()
        private set

    var currencyColStats : List<CurrencyStats> = listOf()
        private set

    var territorySummaryStats : Map<String,TerritorySummaryStats> = mapOf()
        private set
    var currencySummaryStats : Map<String,CurrencySummaryStats> = mapOf()
        private set

    companion object {
        private var _repository : BanknotesCatalogRepository? = null

        fun create(
            ctx : Context,
            banknotesApiClient : BanknotesAPIClient,
            continentCacheRepository: ContinentCacheRepository,
            terTypeCacheRepository: TerritoryTypeCacheRepository
        ) : BanknotesCatalogRepository {
            if (_repository==null)
                _repository = BanknotesCatalogRepository(
                    FlagsLocalDataSource(ctx.assets),
                    BanknotesNetworkDataSource(banknotesApiClient),
                    continentCacheRepository,
                    terTypeCacheRepository)

            return _repository as BanknotesCatalogRepository
        }
    }


    suspend fun fetchContinents() {
        val cache = continentCacheRepository.continentCacheFlow.first()

        if (cache.isNotEmpty()) {
            Log.i("App-Repository", "Continents retrieved from Cache")
            continents = cache.associateBy { it.id }
        }
        else{
            Log.i("App-Repository", "Sending Network request to getContinents")
            val result = banknotesNetworkDataSource.getContinents()
            continents = result.associateBy { it.id }

            // Write Continents to Cache (Preferences)
            continentCacheRepository.updateContinents(result)
        }
    }

    suspend fun fetchTerritoryTypes() {
        val cache = terTypeCacheRepository.territoryTypeCacheFlow.first()

        if (cache.isNotEmpty()) {
            Log.i("App-Repository", "Territory Types retrieved from Cache")
            territoryTypes = cache.associateBy { it.id }
        }
        else{
            Log.i("App-Repository", "Sending Network request to getTerritoryTypes")
            val result = banknotesNetworkDataSource.getTerritoryTypes()
            territoryTypes = result.associateBy { it.id }

            // Write to Cache (Preferences)
            terTypeCacheRepository.updateTerritoryTypes(result)
        }

    }

    // Use after Continents are fetched !!!
    suspend fun fetchTerritories() {
        coroutineScope { launch{fetchFlags()} }
        territories = banknotesNetworkDataSource.getTerritories().filter { ter ->
            continents[ter.continent.id]  != null
        }
    }
    suspend fun fetchTerritoryStats() {
        territoryCatStats = banknotesNetworkDataSource.getTerritoryStats()
    }

    suspend fun fetchCurrencies() {
        currencies = banknotesNetworkDataSource.getCurrencies()
    }
    suspend fun fetchCurrencyStats() {
        currencyCatStats = banknotesNetworkDataSource.getCurrencyStats()
    }

    fun setStats(continentId : UInt? = null){
        val tmp = mutableMapOf<String, TerritorySummaryStats>()
        val territoriesByCont = if (continentId!= null) territories.filter { it.continent.id == continentId } else territories
        territoryTypes.forEach { type ->
            val terList = territoriesByCont.filter { it.territoryType.id == type.key }
            var extinctCount = 0
            var currentCount = 0
            var extinctIssuerCount = 0
            var currentIssuerCount = 0
            terList.forEach{ter ->
                val isIssuer = currencies.find { cur -> cur.ownedBy.find { owner -> owner.territory.id == ter.id } != null } != null
                if (ter.end == null) {
                    currentCount++
                    if (isIssuer) currentIssuerCount++
                } else {
                    extinctCount++
                    if (isIssuer) extinctIssuerCount++
                }
            }
            tmp[type.value.name] = TerritorySummaryStats(current = TerritorySummaryStats.Data(currentCount,currentIssuerCount), extinct = TerritorySummaryStats.Data(extinctCount,extinctIssuerCount))
        }
        territorySummaryStats = tmp

        val tmpCur = mutableMapOf<String, CurrencySummaryStats>()
        val currenciesByCont = if (continentId!= null) currencies.filter { it.continent.id == continentId } else currencies

        var extinctCount = 0
        var currentCount = 0
        var sharedExtinctCount = 0
        var sharedCurrentCount = 0
        currenciesByCont.forEach {cur ->
            val isShared = (cur.sharedBy?.size ?: 0) > 0
            if (cur.end == null) {
                currentCount++
                if (isShared) sharedCurrentCount++
            } else {
                extinctCount++
                if (isShared) sharedExtinctCount++
            }
        }
        tmpCur["Total"] = CurrencySummaryStats(current = CurrencySummaryStats.Data(currentCount), extinct = CurrencySummaryStats.Data(extinctCount) )
        tmpCur["Shared"] = CurrencySummaryStats(current = CurrencySummaryStats.Data(sharedCurrentCount), extinct = CurrencySummaryStats.Data(sharedExtinctCount) )

        currencySummaryStats = tmpCur
    }

    fun sortTerritories(sortBy : TerritorySortableField, statsCol : StatsSubColumn?, sortingDir : SortDirection){
        territories = when (sortBy){
            TerritoryFieldName -> if (sortingDir == SortDirection.DESC) territories.sortedByDescending { it.name  } else territories.sortedBy { it.name  }
            TerritoryFieldStart -> if (sortingDir == SortDirection.DESC) territories.sortedByDescending { it.start } else territories.sortedBy { it.start }
            TerritoryFieldEnd -> if (sortingDir == SortDirection.DESC) territories.sortedByDescending { it.end } else territories.sortedBy { it.end }
            TerritoryFieldCurrencies -> {
                if (statsCol == StatsSubColumn.CATALOG){
                    if (sortingDir == SortDirection.DESC)
                        territories.sortedByDescending { territoryCatStats.find { it2 -> it2.id == it.id }?.numCurrencies?:0 }
                    else
                        territories.sortedBy { territoryCatStats.find { it2 -> it2.id == it.id }?.numCurrencies?:0 }
                }
                else{
                    if (sortingDir == SortDirection.DESC)
                        territories.sortedByDescending { territoryColStats.find { it2 -> it2.id == it.id }?.numCurrencies?:0 }
                    else
                        territories.sortedBy { territoryColStats.find { it2 -> it2.id == it.id }?.numCurrencies?:0 }
                }
            }
            TerritoryFieldIssues -> {
                if (statsCol == StatsSubColumn.CATALOG){
                    if (sortingDir == SortDirection.DESC)
                        territories.sortedByDescending { territoryCatStats.find { it2 -> it2.id == it.id }?.numSeries?:0 }
                    else
                        territories.sortedBy { territoryCatStats.find { it2 -> it2.id == it.id }?.numSeries?:0 }
                }
                else{
                    if (sortingDir == SortDirection.DESC)
                        territories.sortedByDescending { territoryColStats.find { it2 -> it2.id == it.id }?.numSeries?:0 }
                    else
                        territories.sortedBy { territoryColStats.find { it2 -> it2.id == it.id }?.numSeries?:0 }
                }
            }
            TerritoryFieldDenominations -> {
                if (statsCol == StatsSubColumn.CATALOG){
                    if (sortingDir == SortDirection.DESC)
                        territories.sortedByDescending { territoryCatStats.find { it2 -> it2.id == it.id }?.numDenominations?:0 }
                    else
                        territories.sortedBy { territoryCatStats.find { it2 -> it2.id == it.id }?.numDenominations?:0 }
                }
                else{
                    if (sortingDir == SortDirection.DESC)
                        territories.sortedByDescending { territoryColStats.find { it2 -> it2.id == it.id }?.numDenominations?:0 }
                    else
                        territories.sortedBy { territoryColStats.find { it2 -> it2.id == it.id }?.numDenominations?:0 }
                }
            }
            TerritoryFieldNotes -> {
                if (statsCol == StatsSubColumn.CATALOG){
                    if (sortingDir == SortDirection.DESC)
                        territories.sortedByDescending { territoryCatStats.find { it2 -> it2.id == it.id }?.numNotes?:0 }
                    else
                        territories.sortedBy { territoryCatStats.find { it2 -> it2.id == it.id }?.numNotes?:0 }
                }
                else{
                    if (sortingDir == SortDirection.DESC)
                        territories.sortedByDescending { territoryColStats.find { it2 -> it2.id == it.id }?.numNotes?:0 }
                    else
                        territories.sortedBy { territoryColStats.find { it2 -> it2.id == it.id }?.numNotes?:0 }
                }
            }
            TerritoryFieldVariants -> {
                if (statsCol == StatsSubColumn.CATALOG){
                    if (sortingDir == SortDirection.DESC)
                        territories.sortedByDescending { territoryCatStats.find { it2 -> it2.id == it.id }?.numVariants?:0 }
                    else
                        territories.sortedBy { territoryCatStats.find { it2 -> it2.id == it.id }?.numVariants?:0 }
                }
                else{
                    if (sortingDir == SortDirection.DESC)
                        territories.sortedByDescending { territoryColStats.find { it2 -> it2.id == it.id }?.numVariants?:0 }
                    else
                        territories.sortedBy { territoryColStats.find { it2 -> it2.id == it.id }?.numVariants?:0 }
                }
            }
            TerritoryFieldPrice -> {
                if (sortingDir == SortDirection.DESC)
                    territories.sortedByDescending { territoryColStats.find { it2 -> it2.id == it.id }?.price?:0f }
                else
                    territories.sortedBy { territoryColStats.find { it2 -> it2.id == it.id }?.price?:0f }
            }
        }
    }

    fun sortCurrencies(sortBy : CurrencySortableField, statsCol : StatsSubColumn?, sortingDir : SortDirection){
        currencies = when (sortBy){
            CurrencyFieldName ->
                if (sortingDir == SortDirection.DESC) currencies.sortedByDescending { it.name }
                else currencies.sortedBy { it.name }
            CurrencyFieldOwnedBy ->
                if (sortingDir == SortDirection.DESC) currencies.sortedByDescending { (it.ownedBy.maxBy { it2 -> it2.start }).territory.name }
                else currencies.sortedBy { (it.ownedBy.maxBy { it2 -> it2.start }).territory.name }
            CurrencyFieldStart ->
                if (sortingDir == SortDirection.DESC) currencies.sortedByDescending { it.startYear }
                else currencies.sortedBy { it.startYear }
            CurrencyFieldEnd ->
                if (sortingDir == SortDirection.DESC) currencies.sortedByDescending { it.endYear }
                else currencies.sortedBy { it.endYear }
            CurrencyFieldIssues -> {
                if (statsCol == StatsSubColumn.CATALOG){
                    if (sortingDir == SortDirection.DESC)
                        currencies.sortedByDescending { currencyCatStats.find { it2 -> it2.id == it.id }?.numSeries?:0 }
                    else
                        currencies.sortedBy { currencyCatStats.find { it2 -> it2.id == it.id }?.numSeries?:0 }
                }
                else{
                    if (sortingDir == SortDirection.DESC)
                        currencies.sortedByDescending { currencyColStats.find { it2 -> it2.id == it.id }?.numSeries?:0 }
                    else
                        currencies.sortedBy { currencyColStats.find { it2 -> it2.id == it.id }?.numSeries?:0 }
                }
            }
            CurrencyFieldDenominations -> {
                if (statsCol == StatsSubColumn.CATALOG){
                    if (sortingDir == SortDirection.DESC)
                        currencies.sortedByDescending { currencyCatStats.find { it2 -> it2.id == it.id }?.numDenominations?:0 }
                    else
                        currencies.sortedBy { currencyCatStats.find { it2 -> it2.id == it.id }?.numDenominations?:0 }
                }
                else{
                    if (sortingDir == SortDirection.DESC)
                        currencies.sortedByDescending { currencyColStats.find { it2 -> it2.id == it.id }?.numDenominations?:0 }
                    else
                        currencies.sortedBy { currencyColStats.find { it2 -> it2.id == it.id }?.numDenominations?:0 }
                }
            }
            CurrencyFieldNotes -> {
                if (statsCol == StatsSubColumn.CATALOG){
                    if (sortingDir == SortDirection.DESC)
                        currencies.sortedByDescending { currencyCatStats.find { it2 -> it2.id == it.id }?.numNotes?:0 }
                    else
                        currencies.sortedBy { currencyCatStats.find { it2 -> it2.id == it.id }?.numNotes?:0 }
                }
                else{
                    if (sortingDir == SortDirection.DESC)
                        currencies.sortedByDescending { currencyColStats.find { it2 -> it2.id == it.id }?.numNotes?:0 }
                    else
                        currencies.sortedBy { currencyColStats.find { it2 -> it2.id == it.id }?.numNotes?:0 }
                }
            }
            CurrencyFieldVariants -> {
                if (statsCol == StatsSubColumn.CATALOG){
                    if (sortingDir == SortDirection.DESC)
                        currencies.sortedByDescending { currencyCatStats.find { it2 -> it2.id == it.id }?.numVariants?:0 }
                    else
                        currencies.sortedBy { currencyCatStats.find { it2 -> it2.id == it.id }?.numVariants?:0 }
                }
                else{
                    if (sortingDir == SortDirection.DESC)
                        currencies.sortedByDescending { currencyColStats.find { it2 -> it2.id == it.id }?.numVariants?:0 }
                    else
                        currencies.sortedBy { currencyColStats.find { it2 -> it2.id == it.id }?.numVariants?:0 }
                }
            }
            CurrencyFieldPrice -> {
                if (sortingDir == SortDirection.DESC)
                    currencies.sortedByDescending { currencyColStats.find { it2 -> it2.id == it.id }?.price?:0f }
                else
                    currencies.sortedBy { currencyColStats.find { it2 -> it2.id == it.id }?.price?:0f }
            }
        }
    }


    fun getTerritories (
        byContinent : UInt?,
        byType : List<TerritoryTypeEnum>?,
        isExisting : Boolean,
        isExtinct : Boolean,
        byFoundedDates: FilterDates,
        byExtinctDates: FilterDates
        ) : List<Territory>
    {
        var result = territories
        if (byContinent != null) result = result.filter { ter -> ter.continent.id == byContinent }
        if (byType != null) result = result.filter { ter -> byType.find { it.name == territoryTypes[ter.territoryType.id]?.abbreviation} != null }
        if (isExisting && !isExtinct ) result = result.filter { ter -> ter.end == null }
        if (!isExisting && isExtinct ) result = result.filter { ter -> ter.end != null }
        if (byFoundedDates.from != null || byFoundedDates.to != null) result = result.filter { ter -> byFoundedDates.from?.let { ter.start >= it }?:true && byFoundedDates.to?.let { ter.start <= it }?:true }
        if (byExtinctDates.from != null || byExtinctDates.to != null) result = result.filter { ter -> byExtinctDates.from?.let { ter.end != null && ter.end >= it }?:true && byExtinctDates.to?.let { ter.end != null && ter.end <= it }?:true }
        return result
    }

    fun getCurrencies (byContinent : UInt) : List<Currency> {
        return currencies.filter { it.continent.id == byContinent }
    }

    private suspend fun fetchFlags() {
        flags = flagsLocalDataSource.getFlags()
    }

}
