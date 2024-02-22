package com.pako2k.banknotescatalog.app

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.data.BanknotesCatalogRepository
import com.pako2k.banknotescatalog.data.Currency
import com.pako2k.banknotescatalog.data.CurrencySortableField
import com.pako2k.banknotescatalog.data.Territory
import com.pako2k.banknotescatalog.data.TerritorySortableField
import com.pako2k.banknotescatalog.data.TerritorySummaryStats
import com.pako2k.banknotescatalog.data.TerritoryTypes
import com.pako2k.banknotescatalog.data.UserPreferences
import com.pako2k.banknotescatalog.data.UserPreferencesRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch




// Class implementing the Application Logic
class MainViewModel private constructor(
        ctx: Context,
        private val repository : BanknotesCatalogRepository,
        private val userPreferencesRepository: UserPreferencesRepository
    ) : ViewModel() {

    // Private so it cannot be updated outside this MainViewModel
    private val _mainUiState = MutableStateFlow(MainUiState())
    // Public property to read the UI state
    val uiState = _mainUiState.asStateFlow()

    // Private so it cannot be updated outside this MainViewModel
    private val _mainUiInitializationState = MutableStateFlow(MainUiInitializationState())
    // Public property to read the UI state
    val initializationState = _mainUiInitializationState.asStateFlow()

    val userPreferencesState : StateFlow<UserPreferences> = userPreferencesRepository.userPreferencesFlow.map { pref ->
        UserPreferences(pref.favouriteTerritories, pref.favouriteCurrencies, pref.historyTerritories, pref.historyCurrencies )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UserPreferences()
    )

    val continents
        get() = repository.continents

    private var territoriesViewData : List<Territory>

    // returns field values for the Summary Table
    fun territoriesViewData() : List<List<Any?>> {
        val uiData : MutableList<List<Any?>> = mutableListOf()

        for(ter in territoriesViewData){
            val terTypSuffix =
                if( ter.territoryType.name == TerritoryTypes.IND.value) ""
                else " [${repository.territoryTypes[ter.territoryType.id]?.abbreviation}]"
            val stats = repository.territoryCatStats.find { it.id == ter.id }
            uiData.add(
                listOf(
                    repository.flags[ter.flagName],
                    ter.iso3?:"",
                    Pair(ter.id, ter.name + terTypSuffix),
                    ter.start.toString(),
                    ter.end?.toString()?:"",
                    stats?.numCurrencies.toString(),
                    "-",
                    stats?.numSeries.toString(),
                    "-",
                    stats?.numDenominations.toString(),
                    "-",
                    stats?.numNotes.toString(),
                    "-",
                    stats?.numVariants.toString(),
                    "-",
                    "-"
                )
            )
        }

        return uiData
    }

    fun territoryViewData(terId : UInt) : Territory? {
        val ter = (repository.territories.find{ it.id == terId })
        ter?.extend(territoriesList = repository.territories, flags = repository.flags)
        return ter
    }

    private var currenciesViewData : List<Currency>
    fun currenciesViewData() : List<List<Any?>> {
        val uiData : MutableList<List<Any?>> = mutableListOf()

        for(cur in currenciesViewData){
            val ownedBy = cur.ownedBy.maxBy { it.start }
            uiData.add(
                listOf(
                    cur.iso3 ?: "",
                    Pair(cur.id, cur.name),
                    Pair(ownedBy.territory.id, ownedBy.territory.name),
                    cur.startYear.toString(),
                    cur.endYear?.toString() ?: ""
                )
            )
        }
        return uiData
    }
    fun currencyViewData(curId : UInt) : Currency? {
        val cur = (repository.currencies.find{ it.id == curId })
        cur?.extend(territoriesList = repository.territories, flags = repository.flags, currenciesList = repository.currencies)
        return cur
    }

    fun getTerritoryStats() : Map<String, TerritorySummaryStats> {
        return repository.territorySummaryStats
    }

    // ViewModel can only be created by ViewModelProvider.Factory
    companion object {
        val Factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BanknotesCatalogApplication
                Log.d(application.getString(R.string.app_log_tag), "Create MainViewModel")

                MainViewModel(
                    application.applicationContext,
                    application.repository,
                    application.userPreferencesRepository
                )
            }
        }
    }

    init {
        Log.d(ctx.getString(R.string.app_log_tag), "Start INIT MainViewModel")

        territoriesViewData = listOf()
        currenciesViewData = listOf()

        // Initialization in separate coroutines
        val jobs = mutableListOf<Deferred<ComponentState>>()

        viewModelScope.async {
            Log.d(ctx.getString(R.string.app_log_tag), "Start asynchronous getContinents")

            // Get Continents and territories
            val result = try {
                repository.fetchContinents()
                repository.fetchTerritoryTypes()

                ComponentState.DONE
            }
            catch (exc : Exception){
                Log.e(ctx.getString(R.string.app_log_tag), exc.toString())
                ComponentState.FAILED
            }

            Log.d(ctx.getString(R.string.app_log_tag), "End asynchronous getContinents")

            return@async result
        }.let { jobs.add(it) }

        viewModelScope.async {
            Log.d(ctx.getString(R.string.app_log_tag), "Start asynchronous getTerritories")

            // Get Territories
            val result : ComponentState = try {
                repository.fetchTerritories()

                territoriesViewData = repository.territories
                ComponentState.DONE
            }
            catch (exc : Exception){
                Log.e(ctx.getString(R.string.app_log_tag), exc.toString())
                ComponentState.FAILED
            }

            Log.d(ctx.getString(R.string.app_log_tag), "End asynchronous getTerritories with $result")
            return@async result
        }.let { jobs.add(it) }

        viewModelScope.async {
            Log.d(ctx.getString(R.string.app_log_tag), "Start asynchronous getTerritories")

            // Get Territory Stats
            val result : ComponentState = try {
                repository.fetchTerritoryStats()
                ComponentState.DONE
            }
            catch (exc : Exception){
                Log.e(ctx.getString(R.string.app_log_tag), exc.toString())
                ComponentState.FAILED
            }

            Log.d(ctx.getString(R.string.app_log_tag), "End asynchronous getTerritories with $result")
            return@async result
        }.let { jobs.add(it) }

        viewModelScope.async {
            Log.d(ctx.getString(R.string.app_log_tag), "Start asynchronous getCurrencies")

            // Get Currencies
            val result : ComponentState = try {
                repository.fetchCurrencies()

                currenciesViewData = repository.currencies
                ComponentState.DONE
            }
            catch (exc : Exception){
                Log.e(ctx.getString(R.string.app_log_tag), exc.message + exc.stackTrace.toString())
                ComponentState.FAILED
            }

            Log.d(ctx.getString(R.string.app_log_tag), "End asynchronous getCurrencies with $result")
            return@async result
        }.let { jobs.add(it) }

        viewModelScope.launch{
            val results = jobs.awaitAll()
            val finalResult =
                if (results.contains(ComponentState.FAILED))
                    ComponentState.FAILED
                else {
                    repository.setStats()
                    ComponentState.DONE
                }

            if (finalResult == ComponentState.DONE)
                _mainUiInitializationState.update {currentState ->
                    currentState.copy(
                        state = finalResult
                    )
                }
        }

        Log.d(ctx.getString(R.string.app_log_tag), "End INIT MainViewModel")
    }


    fun updateFavouriteTer(id : UInt){
        viewModelScope.launch {
            userPreferencesRepository.updateFavTer(id)
        }
    }

    fun updateHistoryTer(id : UInt){
        viewModelScope.launch {
            userPreferencesRepository.updateHistTer(id)
        }
    }

    fun updateFavouriteCur(id : UInt){
        viewModelScope.launch {
            userPreferencesRepository.updateFavCur(id)
        }
    }

    fun updateHistoryCur(id : UInt){
        viewModelScope.launch {
            userPreferencesRepository.updateHistCur(id)
        }
    }

    fun getCurrencyBookmark(id: UInt) : String? {
        val cur = currencyViewData(id) ?: return null

        var bookmark = cur.name
        if (cur.iso3 != null){
            bookmark += " (${cur.iso3})"
        }
        val mainOwner = cur.ownedByExt.maxBy { it.start }.territory.name
        bookmark += " - $mainOwner"
        return bookmark
    }

    fun setContinentFilter(continentId : UInt) {
        val newSelection =
            if (continentId == uiState.value.selectedContinent) null
            else continentId

        if (newSelection != null) {
            territoriesViewData = repository.getTerritories(newSelection)
            currenciesViewData = repository.getCurrencies(newSelection)
        }
        else{
            territoriesViewData = repository.territories
            currenciesViewData = repository.currencies
        }

        repository.setStats(newSelection)

        _mainUiState.update { currentState ->
            currentState.copy(
                selectedContinent = newSelection
            )
        }
    }

    fun sortTerritoriesBy(sortBy : TerritorySortableField, statsCol : StatsSubColumn?) {

        _mainUiState.value.territoriesTable.sortBy( _mainUiState.value.territoriesTable.getCol(sortBy)?:2, statsCol)

        val sortedColumn = _mainUiState.value.territoriesTable.sortedBy
        val newSortingDir = _mainUiState.value.territoriesTable.columns[sortedColumn].sortedDirection!!
        repository.sortTerritories(sortBy, statsCol, newSortingDir )


        territoriesViewData = _mainUiState.value.selectedContinent?.let {repository.getTerritories(it)}?:repository.territories

        _mainUiState.update { currentState ->
            currentState.copy(
                summaryTableSortingFlag = !currentState.summaryTableSortingFlag
            )
        }
    }

    fun sortCurrenciesBy(sortBy : CurrencySortableField, statsCol : StatsSubColumn?) {
        _mainUiState.value.currenciesTable.sortBy(_mainUiState.value.currenciesTable.getCol(sortBy)?:1, statsCol)
        val sortedColumn = _mainUiState.value.currenciesTable.sortedBy
        val newSortingDir = _mainUiState.value.currenciesTable.columns[sortedColumn].sortedDirection!!

        repository.sortCurrencies(sortBy, newSortingDir)

        currenciesViewData = _mainUiState.value.selectedContinent?.let {repository.getCurrencies(it)}?:repository.currencies

        _mainUiState.update { currentState ->
            currentState.copy(
                summaryTableSortingFlag = !currentState.summaryTableSortingFlag
            )
        }
    }

    fun showTerritoryStats(visible: Boolean){
        _mainUiState.update { currentState ->
            currentState.copy(
                showTerritoryStats = visible
            )
        }
    }
}