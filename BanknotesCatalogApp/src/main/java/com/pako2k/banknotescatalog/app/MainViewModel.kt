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
import com.pako2k.banknotescatalog.data.CurrencyFieldDenominations
import com.pako2k.banknotescatalog.data.CurrencyFieldEnd
import com.pako2k.banknotescatalog.data.CurrencyFieldIssues
import com.pako2k.banknotescatalog.data.CurrencyFieldNotes
import com.pako2k.banknotescatalog.data.CurrencyFieldPrice
import com.pako2k.banknotescatalog.data.CurrencyFieldStart
import com.pako2k.banknotescatalog.data.CurrencyFieldVariants
import com.pako2k.banknotescatalog.data.CurrencySortableField
import com.pako2k.banknotescatalog.data.CurrencySummaryStats
import com.pako2k.banknotescatalog.data.FilterDates
import com.pako2k.banknotescatalog.data.ShowPreferenceEnum
import com.pako2k.banknotescatalog.data.ShowPreferences
import com.pako2k.banknotescatalog.data.ShowPreferencesRepository
import com.pako2k.banknotescatalog.data.Territory
import com.pako2k.banknotescatalog.data.TerritoryFieldCurrencies
import com.pako2k.banknotescatalog.data.TerritoryFieldDenominations
import com.pako2k.banknotescatalog.data.TerritoryFieldEnd
import com.pako2k.banknotescatalog.data.TerritoryFieldIssues
import com.pako2k.banknotescatalog.data.TerritoryFieldNotes
import com.pako2k.banknotescatalog.data.TerritoryFieldPrice
import com.pako2k.banknotescatalog.data.TerritoryFieldStart
import com.pako2k.banknotescatalog.data.TerritoryFieldVariants
import com.pako2k.banknotescatalog.data.TerritorySortableField
import com.pako2k.banknotescatalog.data.TerritorySummaryStats
import com.pako2k.banknotescatalog.data.TerritoryTypeEnum
import com.pako2k.banknotescatalog.data.UserPreferences
import com.pako2k.banknotescatalog.data.UserPreferencesRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


private const val MAX_RETRIES = 2


// Class implementing the Application Logic
class MainViewModel private constructor(
    ctx: Context,
    private val repository : BanknotesCatalogRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val showPreferencesRepository: ShowPreferencesRepository
    ) : ViewModel() {

    // Private so it cannot be updated outside this MainViewModel
    private val _mainUiState = MutableStateFlow(MainUiState())

    // Public property to read the UI state
    val uiState = _mainUiState.asStateFlow()

    // Private so it cannot be updated outside this MainViewModel
    private val _mainUiInitializationState = MutableStateFlow(MainUiInitializationState())
    // Public property to read the UI state
    val initializationState = _mainUiInitializationState.asStateFlow()

    val userPreferencesState = userPreferencesRepository.userPreferencesFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UserPreferences()
    )

    val showPreferencesState = showPreferencesRepository.showPreferencesFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ShowPreferences()
    )

    val continents
        get() = repository.continents

    private var territoriesViewData : List<Territory> = listOf()
        set(value) {
            field = value
            val uiData : MutableList<List<Any?>> = mutableListOf()
            for(ter in value){
                val terTypSuffix =
                    if( ter.territoryType.name == TerritoryTypeEnum.Ind.value) ""
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
            territoriesViewDataUI = uiData
        }

    /*
        Field values for the Summary Table
        Property automatically set when the territoriesViewData is modified
    */
    lateinit var territoriesViewDataUI : List<List<Any?>>
        private set



    fun territoryViewData(terId : UInt) : Territory? {
        val ter = (repository.territories.find{ it.id == terId })
        ter?.extend(territoriesList = repository.territories, flags = repository.flags)
        return ter
    }


    private var currenciesViewData : List<Currency> = listOf()
        set(value) {
            field = value

            val uiData : MutableList<List<Any?>> = mutableListOf()
            for(cur in value){
                val ownedBy = cur.ownedBy.maxBy { it.start }
                val stats = repository.currencyCatStats.find { it.id == cur.id }
                uiData.add(
                    listOf(
                        cur.iso3 ?: "",
                        Pair(cur.id, cur.name),
                        Pair(ownedBy.territory.id, ownedBy.territory.name),
                        cur.startYear.toString(),
                        cur.endYear?.toString() ?: "",
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
            currenciesViewDataUI = uiData
        }

    /*
        Field values for the Summary Table
        Property automatically set when the currenciesViewData is modified
    */
    lateinit var currenciesViewDataUI : List<List<Any?>>
        private set

    fun currencyViewData(curId : UInt) : Currency? {
        val cur = (repository.currencies.find{ it.id == curId })
        cur?.extend(territoriesList = repository.territories, flags = repository.flags, currenciesList = repository.currencies)
        return cur
    }

    fun getTerritoryStats() : Map<String, TerritorySummaryStats> {
        return repository.territorySummaryStats
    }

    fun getCurrencyStats() : Map<String, CurrencySummaryStats> {
        return repository.currencySummaryStats
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
                    application.userPreferencesRepository,
                    application.showPreferencesRepository
                )
            }
        }
    }

    init {
        Log.d(ctx.getString(R.string.app_log_tag), "Start INIT MainViewModel")

        // Initialization in separate coroutines
        val jobs = mutableListOf<Deferred<ComponentState>>()

        viewModelScope.async {
            Log.d(ctx.getString(R.string.app_log_tag), "Start asynchronous getContinents, getTerritoryTypes and getTerritories")
            var retryCount = 0
            var result = ComponentState.LOADING
            while (retryCount < MAX_RETRIES) {
                result = try {
                    repository.fetchContinents()
                    repository.fetchTerritoryTypes()
                    repository.fetchTerritories()

                    // Set the data to be shown in UI
                    territoriesViewData = repository.territories
                    retryCount = MAX_RETRIES
                    ComponentState.DONE

                } catch (exc: Exception) {
                    Log.e(ctx.getString(R.string.app_log_tag), exc.toString() + " - " + exc.cause)
                    retryCount++
                    ComponentState.FAILED
                }
            }

            Log.d(ctx.getString(R.string.app_log_tag), "End asynchronous getContinents, getTerritoryTypes and getTerritories with $result")
            return@async result
        }.let { jobs.add(it) }

        viewModelScope.async {
            Log.d(ctx.getString(R.string.app_log_tag), "Start asynchronous getTerritoryStats")

            // Get Territory Stats
            var retryCount = 0
            var result = ComponentState.LOADING
            while (retryCount < MAX_RETRIES) {
                result = try {
                    repository.fetchTerritoryStats()
                    retryCount = MAX_RETRIES
                    ComponentState.DONE
                } catch (exc: Exception) {
                    Log.e(ctx.getString(R.string.app_log_tag), exc.toString() + " - " + exc.cause)
                    retryCount++
                    ComponentState.FAILED
                }
            }

            Log.d(ctx.getString(R.string.app_log_tag), "End asynchronous getTerritoryStats with $result")
            return@async result
        }.let { jobs.add(it) }

        viewModelScope.async {
            Log.d(ctx.getString(R.string.app_log_tag), "Start asynchronous getCurrencies")

            // Get Currencies
            var retryCount = 0
            var result = ComponentState.LOADING
            while (retryCount < MAX_RETRIES) {
                result = try {
                    repository.fetchCurrencies()

                    currenciesViewData = repository.currencies
                    retryCount = MAX_RETRIES
                    ComponentState.DONE
                } catch (exc: Exception) {
                    Log.e(ctx.getString(R.string.app_log_tag), exc.toString() + " - " + exc.cause)
                    retryCount++
                    ComponentState.FAILED
                }
            }

            Log.d(ctx.getString(R.string.app_log_tag), "End asynchronous getCurrencies with $result")
            return@async result
        }.let { jobs.add(it) }

        viewModelScope.async {
            Log.d(ctx.getString(R.string.app_log_tag), "Start asynchronous getCurrencyStats")

            // Get Currency Stats
            var retryCount = 0
            var result = ComponentState.LOADING
            while (retryCount < MAX_RETRIES) {
                result = try {
                    repository.fetchCurrencyStats()
                    retryCount = MAX_RETRIES
                    ComponentState.DONE
                } catch (exc: Exception) {
                    Log.e(ctx.getString(R.string.app_log_tag), exc.toString() + " - " + exc.cause)
                    retryCount++
                    ComponentState.FAILED
                }
            }

            Log.d(ctx.getString(R.string.app_log_tag), "End asynchronous getCurrencyStats with $result")
            return@async result
        }.let { jobs.add(it) }

        viewModelScope.launch {
            val preferences = showPreferencesRepository.showPreferencesFlow.first()
            if (!preferences.showDates){
                _mainUiState.value.territoriesTable.showCol(TerritoryFieldStart, false)
                _mainUiState.value.territoriesTable.showCol(TerritoryFieldEnd, false)
                _mainUiState.value.currenciesTable.showCol(CurrencyFieldStart, false)
                _mainUiState.value.currenciesTable.showCol(CurrencyFieldEnd, false)
            }
            if (!preferences.showCurrencies)
                _mainUiState.value.territoriesTable.showCol(TerritoryFieldCurrencies, false)
            if (!preferences.showIssues) {
                _mainUiState.value.territoriesTable.showCol(TerritoryFieldIssues, false)
                _mainUiState.value.currenciesTable.showCol(CurrencyFieldIssues, false)
            }
            if (!preferences.showFaceValues) {
                _mainUiState.value.territoriesTable.showCol(TerritoryFieldDenominations, false)
                _mainUiState.value.currenciesTable.showCol(CurrencyFieldDenominations, false)
            }
            if (!preferences.showNoteTypes) {
                _mainUiState.value.territoriesTable.showCol(TerritoryFieldNotes, false)
                _mainUiState.value.currenciesTable.showCol(CurrencyFieldNotes, false)
            }
            if (!preferences.showVariants) {
                _mainUiState.value.territoriesTable.showCol(TerritoryFieldVariants, false)
                _mainUiState.value.currenciesTable.showCol(CurrencyFieldVariants, false)
            }
            if (!preferences.showPrice) {
                _mainUiState.value.territoriesTable.showCol(TerritoryFieldPrice, false)
                _mainUiState.value.currenciesTable.showCol(CurrencyFieldPrice, false)
            }
        }

        viewModelScope.launch{
            Log.d(ctx.getString(R.string.app_log_tag), "Waiting for all jobs...")
            val results = jobs.awaitAll()
            Log.d(ctx.getString(R.string.app_log_tag), "Jobs finished with result: $results")
            val finalResult =
                if (results.contains(ComponentState.FAILED))
                    ComponentState.FAILED
                else {
                    repository.setStats()
                    ComponentState.DONE
                }

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

    fun updateSettings(showPreference : ShowPreferenceEnum, value : Boolean ){

        viewModelScope.launch {
            showPreferencesRepository.updateShowPreference(showPreference, value)
        }

        when (showPreference){
            ShowPreferenceEnum.KEY_SHOW_DATES -> {
                _mainUiState.value.territoriesTable.showCol(TerritoryFieldStart, value)
                _mainUiState.value.territoriesTable.showCol(TerritoryFieldEnd, value)
                _mainUiState.value.currenciesTable.showCol(CurrencyFieldStart, value)
                _mainUiState.value.currenciesTable.showCol(CurrencyFieldEnd, value)
            }
            ShowPreferenceEnum.KEY_SHOW_CUR -> {
                _mainUiState.value.territoriesTable.showCol(TerritoryFieldCurrencies, value)
            }
            ShowPreferenceEnum.KEY_SHOW_ISSUES -> {
                _mainUiState.value.territoriesTable.showCol(TerritoryFieldIssues, value)
                _mainUiState.value.currenciesTable.showCol(CurrencyFieldIssues, value)
            }
            ShowPreferenceEnum.KEY_SHOW_VALUES -> {
                _mainUiState.value.territoriesTable.showCol(TerritoryFieldDenominations, value)
                _mainUiState.value.currenciesTable.showCol(CurrencyFieldDenominations, value)
            }
            ShowPreferenceEnum.KEY_SHOW_NOTES -> {
                _mainUiState.value.territoriesTable.showCol(TerritoryFieldNotes, value)
                _mainUiState.value.currenciesTable.showCol(CurrencyFieldNotes, value)
            }
            ShowPreferenceEnum.KEY_SHOW_VARIANTS -> {
                _mainUiState.value.territoriesTable.showCol(TerritoryFieldVariants, value)
                _mainUiState.value.currenciesTable.showCol(CurrencyFieldVariants, value)
            }
            ShowPreferenceEnum.KEY_SHOW_PRICES -> {
                _mainUiState.value.territoriesTable.showCol(TerritoryFieldPrice, value)
                _mainUiState.value.currenciesTable.showCol(CurrencyFieldPrice, value)
            }
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

        _mainUiState.update { currentState ->
            currentState.copy(
                selectedContinent = newSelection
            )
        }
        filterTerritories()
        filterCurrencies()
        repository.setStats(newSelection)
    }

    fun sortTerritoriesBy(sortBy : TerritorySortableField, statsCol : StatsSubColumn?) {
        _mainUiState.value.territoriesTable.sortBy( _mainUiState.value.territoriesTable.getCol(sortBy)?:2, statsCol)

        val sortedColumn = _mainUiState.value.territoriesTable.sortedBy
        val newSortingDir = _mainUiState.value.territoriesTable.columns[sortedColumn].sortedDirection!!
        repository.sortTerritories(sortBy, statsCol, newSortingDir )

        // Apply filter to the entire sorted list
        filterTerritories()

        _mainUiState.update { currentState ->
            currentState.copy(
                summaryTableTriggerUpdateFlag = !currentState.summaryTableTriggerUpdateFlag
            )
        }
    }

    fun sortCurrenciesBy(sortBy : CurrencySortableField, statsCol : StatsSubColumn?) {
        _mainUiState.value.currenciesTable.sortBy(_mainUiState.value.currenciesTable.getCol(sortBy)?:1, statsCol)
        val sortedColumn = _mainUiState.value.currenciesTable.sortedBy
        val newSortingDir = _mainUiState.value.currenciesTable.columns[sortedColumn].sortedDirection!!

        repository.sortCurrencies(sortBy, statsCol, newSortingDir)

        filterCurrencies()

        _mainUiState.update { currentState ->
            currentState.copy(
                summaryTableTriggerUpdateFlag = !currentState.summaryTableTriggerUpdateFlag
            )
        }
    }


    fun showTerritoryStats(visible: Boolean){
        _mainUiState.update { currentState ->
            currentState.copy(
                showTerritoryStats = visible,
                showTerritoryFilters = false
            )
        }
    }

    fun showCurrencyStats(visible: Boolean){
        _mainUiState.update { currentState ->
            currentState.copy(
                showCurrencyStats = visible,
                showCurrencyFilters = false
            )
        }
    }

    fun showTerritoryFilters(visible: Boolean){
        _mainUiState.update { currentState ->
            currentState.copy(
                showTerritoryFilters = visible,
                showTerritoryStats = false
            )
        }
    }

    fun showCurrencyFilters(visible: Boolean){
        _mainUiState.update { currentState ->
            currentState.copy(
                showCurrencyFilters = visible,
                showCurrencyStats = false
            )
        }
    }


    fun updateFilterTerritoryType(type : TerritoryTypeEnum, isSelected : Boolean){
        val newMap = _mainUiState.value.filterTerritoryTypes.mapValues { if(it.key == type) isSelected else it.value }

        // At least one type must be selected!!
        if (!newMap.containsValue(true)) return

        _mainUiState.update { currentState ->
            currentState.copy(
                filterTerritoryTypes = newMap
            )
        }
        filterTerritories()
    }

    fun updateFilterTerritoryState(isSelected : Pair<Boolean, Boolean>){
        // At least one state must be true!!
        if (!isSelected.first && !isSelected.second) return

        _mainUiState.update { currentState ->
            currentState.copy(
                filterTerritoryState = isSelected
            )
        }
        filterTerritories()
    }

    fun updateFilterTerritoryFoundedDates(dates : FilterDates){
        _mainUiState.update { currentState ->
            currentState.copy(
                filterTerFounded = dates
            )
        }
        if (dates.isValid) {
            filterTerritories()
        }
    }

    fun updateFilterTerritoryExtinctDates(dates : FilterDates){
        _mainUiState.update { currentState ->
            currentState.copy(
                filterTerExtinct = dates
            )
        }
        if (dates.isValid) {
            filterTerritories()
        }
    }

    fun updateFilterCurrencyType(isSelected : Pair<Boolean, Boolean>){
        // At least one state must be true!!
        if (!isSelected.first && !isSelected.second) return

        _mainUiState.update { currentState ->
            currentState.copy(
                filterCurrencyTypes = isSelected
            )
        }
        filterCurrencies()
    }

    fun updateFilterCurrencyState(isSelected : Pair<Boolean, Boolean>){
        // At least one state must be true!!
        if (!isSelected.first && !isSelected.second) return

        _mainUiState.update { currentState ->
            currentState.copy(
                filterCurrencyState = isSelected
            )
        }
        filterCurrencies()
    }

    fun updateFilterCurrencyFoundedDates(dates : FilterDates){
        _mainUiState.update { currentState ->
            currentState.copy(
                filterCurFounded = dates
            )
        }
        if (dates.isValid) {
            filterCurrencies()
        }
    }

    fun updateFilterCurrencyExtinctDates(dates : FilterDates){
        _mainUiState.update { currentState ->
            currentState.copy(
                filterCurExtinct = dates
            )
        }
        if (dates.isValid) {
            filterCurrencies()
        }
    }

    private fun filterTerritories() {
        val filterTerTypesToList = _mainUiState.value.filterTerritoryTypes.filter { it.value }.keys.toList().let {
            if (it.size == _mainUiState.value.filterTerritoryTypes.size) null else it
        }
        territoriesViewData = repository.getTerritories(
            _mainUiState.value.selectedContinent,
            filterTerTypesToList,
            _mainUiState.value.filterTerritoryState.first,
            _mainUiState.value.filterTerritoryState.second,
            _mainUiState.value.filterTerFounded,
            _mainUiState.value.filterTerExtinct
        )
    }

    private fun filterCurrencies() {
        currenciesViewData = repository.getCurrencies(
            _mainUiState.value.selectedContinent,
            _mainUiState.value.filterCurrencyTypes,
            _mainUiState.value.filterCurrencyState,
            _mainUiState.value.filterCurFounded,
            _mainUiState.value.filterCurExtinct
        )
    }
}