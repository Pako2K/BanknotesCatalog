package com.pako2k.banknotescatalog.data.repo

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import com.pako2k.banknotescatalog.app.StatsSubColumn
import com.pako2k.banknotescatalog.data.Continent
import com.pako2k.banknotescatalog.data.Currency
import com.pako2k.banknotescatalog.data.FilterDates
import com.pako2k.banknotescatalog.data.ItemLinked
import com.pako2k.banknotescatalog.data.Territory
import com.pako2k.banknotescatalog.data.TerritoryType
import com.pako2k.banknotescatalog.data.TerritoryTypeEnum
import com.pako2k.banknotescatalog.data.stats.CurrencyStats
import com.pako2k.banknotescatalog.data.stats.CurrencySummaryStats
import com.pako2k.banknotescatalog.data.stats.DenominationStats
import com.pako2k.banknotescatalog.data.stats.DenominationSummaryStats
import com.pako2k.banknotescatalog.data.stats.IssueYearStats
import com.pako2k.banknotescatalog.data.stats.TerritoryStats
import com.pako2k.banknotescatalog.data.stats.TerritorySummaryStats
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


// Enumeration of Territory fields which can be used for sorting. Implemented as child of SortableField!
sealed class CollectionSortableField : SortableField()
object CollectionFieldTerritory : CollectionSortableField()
object CollectionFieldDenomination : CollectionSortableField()
object CollectionFieldCurrency : CollectionSortableField()
object CollectionFieldPrice : CollectionSortableField()
object CollectionFieldSeller : CollectionSortableField()
object CollectionFieldPurchaseDate : CollectionSortableField()



// Enumeration of IssueYear fields which can be used for sorting. Implemented as child of SortableField!
sealed class IssueYearSortableField : SortableField()
object IssueYearFieldYear : IssueYearSortableField()
object IssueYearFieldTerritories : IssueYearSortableField()
object IssueYearFieldCurrencies : IssueYearSortableField()
object IssueYearFieldIssues : IssueYearSortableField()
object IssueYearFieldDenominations : IssueYearSortableField()
object IssueYearFieldNotes : IssueYearSortableField()
object IssueYearFieldVariants : IssueYearSortableField()
object IssueYearFieldPrice : IssueYearSortableField()


// Enumeration of Denomination fields which can be used for sorting. Implemented as child of SortableField!
sealed class DenomSortableField : SortableField()
object DenomFieldValue : DenomSortableField()
object DenomFieldTerritories : DenomSortableField()
object DenomFieldCurrencies : DenomSortableField()
object DenomFieldNotes : DenomSortableField()
object DenomFieldVariants : DenomSortableField()
object DenomFieldPrice : DenomSortableField()


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

    var denominationCatStats : List<DenominationStats> = listOf()
        private set

    var denominationColStats : List<DenominationStats> = listOf()
        private set

    var issueYearCatStats : List<IssueYearStats> = listOf()
        private set

    var issueYearColStats : List<IssueYearStats> = listOf()
        private set

    var territorySummaryStats : Map<String, TerritorySummaryStats> = mapOf()
        private set
    var currencySummaryStats : Map<String, CurrencySummaryStats> = mapOf()
        private set

    var denominationSummaryStats : DenominationSummaryStats = DenominationSummaryStats(
        DenominationSummaryStats.Data(0,0),
        DenominationSummaryStats.Data(0,0)
    )
        private set

    var collection : List<ItemLinked> = listOf()
        private set

    companion object {
        private var _repository : BanknotesCatalogRepository? = null

        fun create(
            ctx : Context,
            banknotesApiClient : BanknotesAPIClient,
            continentCacheRepository: ContinentCacheRepository,
            terTypeCacheRepository: TerritoryTypeCacheRepository
        ) : BanknotesCatalogRepository {
            if (_repository ==null)
                _repository = BanknotesCatalogRepository(
                    FlagsLocalDataSource(ctx.assets),
                    BanknotesNetworkDataSource(banknotesApiClient),
                    continentCacheRepository,
                    terTypeCacheRepository)

            return _repository as BanknotesCatalogRepository
        }
    }

    suspend fun getUserSession(username : String, password : String) = banknotesNetworkDataSource.getUserSession(username, password)

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

    suspend fun fetchDenominationStats(
        fromYear : Int? = null,
        toYear : Int? = null) {
        denominationCatStats = banknotesNetworkDataSource.getDenominationStats(fromYear,toYear)
    }

    suspend fun fetchIssueYearStats() {
        issueYearCatStats = banknotesNetworkDataSource.getIssueYearStats()
    }

    suspend fun fetchCollection(sessionId : String){
        collection = banknotesNetworkDataSource.getCollection(sessionId)
    }

    fun setTerritoryStats(continentId: UInt?){
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
    }

    fun setCurrencyStats(continentId: UInt?){
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

    fun setDenominationStats(continentId : UInt?){
        var totalExtinctCount = 0
        var totalCurrentCount = 0
        denominationCatStats.forEach {den ->
            if (continentId == null){
                if(den.totalStats.isCurrent) totalCurrentCount++
                else totalExtinctCount++
            }
            else{
                den.continentStats.find { it.id == continentId }?.let {
                    if (it.isCurrent)  totalCurrentCount++
                    else totalExtinctCount++
                }
            }
        }
        denominationSummaryStats = DenominationSummaryStats(current = DenominationSummaryStats.Data(totalCurrentCount), extinct = DenominationSummaryStats.Data(totalExtinctCount) )
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


    fun sortDenominations(sortBy : DenomSortableField, statsCol : StatsSubColumn?, sortingDir : SortDirection, continentId : UInt?){
        denominationCatStats = when (sortBy){
            DenomFieldValue ->
                if (sortingDir == SortDirection.DESC){
                    denominationCatStats.sortedByDescending { it.denomination }
                }
                else{
                    denominationCatStats.sortedBy { it.denomination }
                }
            DenomFieldTerritories ->
                if (statsCol == StatsSubColumn.CATALOG){
                    if (sortingDir == SortDirection.DESC)
                        denominationCatStats.sortedByDescending { if (continentId == null) it.totalStats.numTerritories else it.continentStats.find { cont -> cont.id == continentId }?.numTerritories?:0 }
                    else
                        denominationCatStats.sortedBy { if (continentId == null) it.totalStats.numTerritories else it.continentStats.find { cont -> cont.id == continentId }?.numTerritories?:0 }
                }
                else{
                    if (sortingDir == SortDirection.DESC)
                        denominationCatStats.sortedByDescending { denominationColStats.find { col -> col.denomination == it.denomination }?.let{ found ->
                            if (continentId == null) found.totalStats.numTerritories else found.continentStats.find { cont -> cont.id == continentId }?.numTerritories?:0 }?:0
                        }
                    else
                        denominationCatStats.sortedBy { denominationColStats.find { col -> col.denomination == it.denomination }?.let{ found ->
                            if (continentId == null) found.totalStats.numTerritories else found.continentStats.find { cont -> cont.id == continentId }?.numTerritories?:0 }?:0
                        }
                }
            DenomFieldCurrencies ->
                if (statsCol == StatsSubColumn.CATALOG){
                    if (sortingDir == SortDirection.DESC)
                        denominationCatStats.sortedByDescending { if (continentId == null) it.totalStats.numCurrencies else it.continentStats.find { cont -> cont.id == continentId }?.numCurrencies?:0 }
                    else
                        denominationCatStats.sortedBy { if (continentId == null) it.totalStats.numCurrencies else it.continentStats.find { cont -> cont.id == continentId }?.numCurrencies?:0 }
                }
                else{
                    if (sortingDir == SortDirection.DESC)
                        denominationCatStats.sortedByDescending { denominationColStats.find { col -> col.denomination == it.denomination }?.let{ found ->
                            if (continentId == null) found.totalStats.numCurrencies else found.continentStats.find { cont -> cont.id == continentId }?.numCurrencies?:0 }?:0
                        }
                    else
                        denominationCatStats.sortedBy { denominationColStats.find { col -> col.denomination == it.denomination }?.let{ found ->
                            if (continentId == null) found.totalStats.numCurrencies else found.continentStats.find { cont -> cont.id == continentId }?.numCurrencies?:0 }?:0
                        }
                }
            DenomFieldNotes ->
                if (statsCol == StatsSubColumn.CATALOG){
                    if (sortingDir == SortDirection.DESC)
                        denominationCatStats.sortedByDescending { if (continentId == null) it.totalStats.numNotes else it.continentStats.find { cont -> cont.id == continentId }?.numNotes?:0 }
                    else
                        denominationCatStats.sortedBy { if (continentId == null) it.totalStats.numNotes else it.continentStats.find { cont -> cont.id == continentId }?.numNotes?:0 }
                }
                else{
                    if (sortingDir == SortDirection.DESC)
                        denominationCatStats.sortedByDescending { denominationColStats.find { col -> col.denomination == it.denomination }?.let{ found ->
                            if (continentId == null) found.totalStats.numNotes else found.continentStats.find { cont -> cont.id == continentId }?.numNotes?:0 }?:0
                        }
                    else
                        denominationCatStats.sortedBy { denominationColStats.find { col -> col.denomination == it.denomination }?.let{ found ->
                            if (continentId == null) found.totalStats.numNotes else found.continentStats.find { cont -> cont.id == continentId }?.numNotes?:0 }?:0
                        }
                }
            DenomFieldVariants ->
                if (statsCol == StatsSubColumn.CATALOG){
                    if (sortingDir == SortDirection.DESC)
                        denominationCatStats.sortedByDescending { if (continentId == null) it.totalStats.numVariants else it.continentStats.find { cont -> cont.id == continentId }?.numVariants?:0 }
                    else
                        denominationCatStats.sortedBy { if (continentId == null) it.totalStats.numVariants else it.continentStats.find { cont -> cont.id == continentId }?.numVariants?:0 }
                }
                else{
                    if (sortingDir == SortDirection.DESC)
                        denominationCatStats.sortedByDescending { denominationColStats.find { col -> col.denomination == it.denomination }?.let{ found ->
                            if (continentId == null) found.totalStats.numVariants else found.continentStats.find { cont -> cont.id == continentId }?.numVariants?:0 }?:0
                        }
                    else
                        denominationCatStats.sortedBy { denominationColStats.find { col -> col.denomination == it.denomination }?.let{ found ->
                            if (continentId == null) found.totalStats.numVariants else found.continentStats.find { cont -> cont.id == continentId }?.numVariants?:0 }?:0
                        }
                }
            DenomFieldPrice -> {
                if (sortingDir == SortDirection.DESC)
                    denominationCatStats.sortedByDescending { denominationColStats.find { it2 -> it2.denomination == it.denomination }?.price?:0f }
                else
                    denominationCatStats.sortedBy { denominationColStats.find { it2 -> it2.denomination == it.denomination }?.price?:0f }
            }
        }
    }


    fun sortIssueYears(sortBy : IssueYearSortableField, statsCol : StatsSubColumn?, sortingDir : SortDirection, continentId : UInt?){
        issueYearCatStats = when (sortBy){
            IssueYearFieldYear ->
                if (sortingDir == SortDirection.DESC){
                    issueYearCatStats.sortedByDescending { it.issueYear }
                }
                else{
                    issueYearCatStats.sortedBy { it.issueYear }
                }
            IssueYearFieldTerritories ->
                if (statsCol == StatsSubColumn.CATALOG){
                    if (sortingDir == SortDirection.DESC)
                        issueYearCatStats.sortedByDescending { if (continentId == null) it.totalStats.numTerritories else it.continentStats.find { cont -> cont.id == continentId }?.numTerritories?:0 }
                    else
                        issueYearCatStats.sortedBy { if (continentId == null) it.totalStats.numTerritories else it.continentStats.find { cont -> cont.id == continentId }?.numTerritories?:0 }
                }
                else{
                    if (sortingDir == SortDirection.DESC)
                        issueYearCatStats.sortedByDescending { issueYearColStats.find { col -> col.issueYear == it.issueYear }?.let{ found ->
                            if (continentId == null) found.totalStats.numTerritories else found.continentStats.find { cont -> cont.id == continentId }?.numTerritories?:0 }?:0
                        }
                    else
                        issueYearCatStats.sortedBy { issueYearColStats.find { col -> col.issueYear == it.issueYear }?.let{ found ->
                            if (continentId == null) found.totalStats.numTerritories else found.continentStats.find { cont -> cont.id == continentId }?.numTerritories?:0 }?:0
                        }
                }
            IssueYearFieldCurrencies ->
                if (statsCol == StatsSubColumn.CATALOG){
                    if (sortingDir == SortDirection.DESC)
                        issueYearCatStats.sortedByDescending { if (continentId == null) it.totalStats.numCurrencies else it.continentStats.find { cont -> cont.id == continentId }?.numCurrencies?:0 }
                    else
                        issueYearCatStats.sortedBy { if (continentId == null) it.totalStats.numCurrencies else it.continentStats.find { cont -> cont.id == continentId }?.numCurrencies?:0 }
                }
                else{
                    if (sortingDir == SortDirection.DESC)
                        issueYearCatStats.sortedByDescending { issueYearColStats.find { col -> col.issueYear == it.issueYear }?.let{ found ->
                            if (continentId == null) found.totalStats.numCurrencies else found.continentStats.find { cont -> cont.id == continentId }?.numCurrencies?:0 }?:0
                        }
                    else
                        issueYearCatStats.sortedBy { issueYearColStats.find { col -> col.issueYear == it.issueYear }?.let{ found ->
                            if (continentId == null) found.totalStats.numCurrencies else found.continentStats.find { cont -> cont.id == continentId }?.numCurrencies?:0 }?:0
                        }
                }
            IssueYearFieldIssues ->
                if (statsCol == StatsSubColumn.CATALOG){
                    if (sortingDir == SortDirection.DESC)
                        issueYearCatStats.sortedByDescending { if (continentId == null) it.totalStats.numSeries else it.continentStats.find { cont -> cont.id == continentId }?.numSeries?:0 }
                    else
                        issueYearCatStats.sortedBy { if (continentId == null) it.totalStats.numSeries else it.continentStats.find { cont -> cont.id == continentId }?.numSeries?:0 }
                }
                else{
                    if (sortingDir == SortDirection.DESC)
                        issueYearCatStats.sortedByDescending { issueYearColStats.find { col -> col.issueYear == it.issueYear }?.let{ found ->
                            if (continentId == null) found.totalStats.numSeries else found.continentStats.find { cont -> cont.id == continentId }?.numSeries?:0 }?:0
                        }
                    else
                        issueYearCatStats.sortedBy { issueYearColStats.find { col -> col.issueYear == it.issueYear }?.let{ found ->
                            if (continentId == null) found.totalStats.numSeries else found.continentStats.find { cont -> cont.id == continentId }?.numSeries?:0 }?:0
                        }
                }
            IssueYearFieldDenominations ->
                if (statsCol == StatsSubColumn.CATALOG){
                    if (sortingDir == SortDirection.DESC)
                        issueYearCatStats.sortedByDescending { if (continentId == null) it.totalStats.numDenominations else it.continentStats.find { cont -> cont.id == continentId }?.numDenominations?:0 }
                    else
                        issueYearCatStats.sortedBy { if (continentId == null) it.totalStats.numDenominations else it.continentStats.find { cont -> cont.id == continentId }?.numDenominations?:0 }
                }
                else{
                    if (sortingDir == SortDirection.DESC)
                        issueYearCatStats.sortedByDescending { issueYearColStats.find { col -> col.issueYear == it.issueYear }?.let{ found ->
                            if (continentId == null) found.totalStats.numDenominations else found.continentStats.find { cont -> cont.id == continentId }?.numDenominations?:0 }?:0
                        }
                    else
                        issueYearCatStats.sortedBy { issueYearColStats.find { col -> col.issueYear == it.issueYear }?.let{ found ->
                            if (continentId == null) found.totalStats.numDenominations else found.continentStats.find { cont -> cont.id == continentId }?.numDenominations?:0 }?:0
                        }
                }
            IssueYearFieldNotes ->
                if (statsCol == StatsSubColumn.CATALOG){
                    if (sortingDir == SortDirection.DESC)
                        issueYearCatStats.sortedByDescending { if (continentId == null) it.totalStats.numNotes else it.continentStats.find { cont -> cont.id == continentId }?.numNotes?:0 }
                    else
                        issueYearCatStats.sortedBy { if (continentId == null) it.totalStats.numNotes else it.continentStats.find { cont -> cont.id == continentId }?.numNotes?:0 }
                }
                else{
                    if (sortingDir == SortDirection.DESC)
                        issueYearCatStats.sortedByDescending { issueYearColStats.find { col -> col.issueYear == it.issueYear }?.let{ found ->
                            if (continentId == null) found.totalStats.numNotes else found.continentStats.find { cont -> cont.id == continentId }?.numNotes?:0 }?:0
                        }
                    else
                        issueYearCatStats.sortedBy { issueYearColStats.find { col -> col.issueYear == it.issueYear }?.let{ found ->
                            if (continentId == null) found.totalStats.numNotes else found.continentStats.find { cont -> cont.id == continentId }?.numNotes?:0 }?:0
                        }
                }
            IssueYearFieldVariants ->
                if (statsCol == StatsSubColumn.CATALOG){
                    if (sortingDir == SortDirection.DESC)
                        issueYearCatStats.sortedByDescending { if (continentId == null) it.totalStats.numVariants else it.continentStats.find { cont -> cont.id == continentId }?.numVariants?:0 }
                    else
                        issueYearCatStats.sortedBy { if (continentId == null) it.totalStats.numVariants else it.continentStats.find { cont -> cont.id == continentId }?.numVariants?:0 }
                }
                else{
                    if (sortingDir == SortDirection.DESC)
                        issueYearCatStats.sortedByDescending { issueYearColStats.find { col -> col.issueYear == it.issueYear }?.let{ found ->
                            if (continentId == null) found.totalStats.numVariants else found.continentStats.find { cont -> cont.id == continentId }?.numVariants?:0 }?:0
                        }
                    else
                        issueYearCatStats.sortedBy { issueYearColStats.find { col -> col.issueYear == it.issueYear }?.let{ found ->
                            if (continentId == null) found.totalStats.numVariants else found.continentStats.find { cont -> cont.id == continentId }?.numVariants?:0 }?:0
                        }
                }
            IssueYearFieldPrice -> {
                if (sortingDir == SortDirection.DESC)
                    issueYearCatStats.sortedByDescending { issueYearColStats.find { it2 -> it2.issueYear == it.issueYear }?.price?:0f }
                else
                    issueYearCatStats.sortedBy { issueYearColStats.find { it2 -> it2.issueYear == it.issueYear }?.price?:0f }
            }
        }
    }


    fun sortCollection(sortBy : CollectionSortableField, sortingDir : SortDirection){
        collection = when (sortBy){
            CollectionFieldTerritory ->
                if (sortingDir == SortDirection.DESC){
                    collection.sortedByDescending { it.territory.name }
                }
                else{
                    collection.sortedBy { it.territory.name }
                }
            CollectionFieldDenomination ->
                if (sortingDir == SortDirection.DESC){
                    collection.sortedByDescending { it.denomination }
                }
                else{
                    collection.sortedBy { it.denomination }
                }
            CollectionFieldCurrency ->
                if (sortingDir == SortDirection.DESC){
                    collection.sortedByDescending { it.currency.name }
                }
                else{
                    collection.sortedBy { it.currency.name }
                }
            CollectionFieldPrice ->
                if (sortingDir == SortDirection.DESC){
                    collection.sortedByDescending { it.item.price }
                }
                else{
                    collection.sortedBy { it.item.price }
                }
            CollectionFieldSeller ->
                if (sortingDir == SortDirection.DESC){
                    collection.sortedByDescending { it.item.seller }
                }
                else{
                    collection.sortedBy { it.item.seller }
                }
            CollectionFieldPurchaseDate ->
                if (sortingDir == SortDirection.DESC){
                    collection.sortedByDescending { it.item.purchaseDate }
                }
                else{
                    collection.sortedBy { it.item.purchaseDate }
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

    fun getCurrencies (
        byContinent : UInt?,
        byType : Pair<Boolean, Boolean>,
        byState : Pair<Boolean, Boolean>,
        byFoundedDates: FilterDates,
        byExtinctDates: FilterDates
    ) : List<Currency> {
        var result = currencies
        if (byContinent != null) result = result.filter { it.continent.id == byContinent }
        if (!byType.first) result = result.filter { it.sharedBy !=  null }
        if (!byType.second) result = result.filter { it.sharedBy == null }
        if (!byState.first) result = result.filter { it.end != null }
        if (!byState.second) result = result.filter { it.end == null }

        if (byFoundedDates.from != null || byFoundedDates.to != null) result = result.filter { cur -> byFoundedDates.from?.let { cur.startYear >= it }?:true && byFoundedDates.to?.let { cur.startYear <= it }?:true }
        if (byExtinctDates.from != null || byExtinctDates.to != null) result = result.filter { cur -> byExtinctDates.from?.let { cur.endYear != null && cur.endYear >= it }?:true && byExtinctDates.to?.let { cur.endYear != null && cur.endYear <= it }?:true }

        return result
    }

    private suspend fun fetchFlags() {
        flags = flagsLocalDataSource.getFlags()
    }

}
