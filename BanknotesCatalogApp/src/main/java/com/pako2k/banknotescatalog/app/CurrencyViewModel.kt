package com.pako2k.banknotescatalog.app

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.data.Currency
import com.pako2k.banknotescatalog.data.FilterDates
import com.pako2k.banknotescatalog.data.repo.BanknotesCatalogRepository
import com.pako2k.banknotescatalog.data.repo.CurrencyFieldDenominations
import com.pako2k.banknotescatalog.data.repo.CurrencyFieldEnd
import com.pako2k.banknotescatalog.data.repo.CurrencyFieldIssues
import com.pako2k.banknotescatalog.data.repo.CurrencyFieldNotes
import com.pako2k.banknotescatalog.data.repo.CurrencyFieldPrice
import com.pako2k.banknotescatalog.data.repo.CurrencyFieldStart
import com.pako2k.banknotescatalog.data.repo.CurrencyFieldVariants
import com.pako2k.banknotescatalog.data.repo.CurrencySortableField
import com.pako2k.banknotescatalog.data.repo.ShowPreferenceEnum
import com.pako2k.banknotescatalog.data.repo.ShowPreferencesRepository
import com.pako2k.banknotescatalog.data.stats.CurrencySummaryStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class CurrencyViewModel private constructor(
    ctx: Context,
    private val repository : BanknotesCatalogRepository,
    private val showPreferencesRepository: ShowPreferencesRepository
) : ViewModel() {

    // Private so it cannot be updated outside this MainViewModel
    private val _currencyUIState = MutableStateFlow(CurrencyUIState())
    // Public property to read the UI state
    val currencyUIState = _currencyUIState.asStateFlow()

    // Private so it cannot be updated outside this MainViewModel
    private val _currencyHasFilterState = MutableStateFlow(false)
    // Public property to read the UI state
    val currencyHasFilterState = _currencyHasFilterState.asStateFlow()

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


    // ViewModel can only be created by ViewModelProvider.Factory
    companion object {
        val Factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BanknotesCatalogApplication
                Log.d(application.getString(R.string.app_log_tag), "Create CurrencyViewModel")

                CurrencyViewModel(
                    application.applicationContext,
                    application.repository,
                    application.showPreferencesRepository
                )
            }
        }
    }

    init {
        Log.d(ctx.getString(R.string.app_log_tag), "Start INIT CurrencyViewModel")

        viewModelScope.launch {
            showPreferencesRepository.showPreferencesFlow.first().let { preferences ->
                preferences.map.forEach {
                    if (!it.value) updateSettings(it.key, false)
                }
            }
        }

        // At this point the repository is already initialized
        currenciesViewData = repository.currencies
        repository.setCurrencyStats(null)
    }

    fun setContinentFilter(selectedContinentId : UInt?) {
        filterCurrencies(selectedContinentId)
        repository.setCurrencyStats(selectedContinentId)

        _currencyUIState.update { currentState ->
            currentState.copy(
                currenciesTableUpdateTrigger = !currentState.currenciesTableUpdateTrigger
            )
        }
    }

    fun sortCurrenciesBy(sortBy : CurrencySortableField, statsCol : StatsSubColumn?, selectedContinent : UInt?) {
        _currencyUIState.value.currenciesTable.sortBy(_currencyUIState.value.currenciesTable.getCol(sortBy)?:1, statsCol)
        val sortedColumn = _currencyUIState.value.currenciesTable.sortedBy
        val newSortingDir = _currencyUIState.value.currenciesTable.columns[sortedColumn].sortedDirection!!

        repository.sortCurrencies(sortBy, statsCol, newSortingDir)

        filterCurrencies(selectedContinent)

        _currencyUIState.update { currentState ->
            currentState.copy(
                currenciesTableUpdateTrigger = !currentState.currenciesTableUpdateTrigger
            )
        }
    }

    fun showStats(visible: Boolean){
        _currencyUIState.update { currentState ->
            currentState.copy(
                showStats = visible,
                showFilters = false
            )
        }
    }
    fun showFilters(visible: Boolean){
        _currencyUIState.update { currentState ->
            currentState.copy(
                showFilters = visible,
                showStats = false
            )
        }
    }

    fun updateFilterCurrencyType(isSelected : Pair<Boolean, Boolean>, selectedContinent : UInt?){
        // At least one state must be true!!
        if (!isSelected.first && !isSelected.second) return

        _currencyUIState.update { currentState ->
            currentState.copy(
                filterCurrencyTypes = isSelected
            )
        }
        updateFiltersState()
        filterCurrencies(selectedContinent)
    }

    fun updateFilterCurrencyState(isSelected : Pair<Boolean, Boolean>, selectedContinent : UInt?){
        // At least one state must be true!!
        if (!isSelected.first && !isSelected.second) return

        _currencyUIState.update { currentState ->
            currentState.copy(
                filterCurrencyState = isSelected
            )
        }
        updateFiltersState()
        filterCurrencies(selectedContinent)
    }

    fun updateFilterCurrencyFoundedDates(dates : FilterDates, selectedContinent : UInt?){
        _currencyUIState.update { currentState ->
            currentState.copy(
                filterCurFounded = dates
            )
        }
        updateFiltersState()
        if (dates.isValid) {
            filterCurrencies(selectedContinent)
        }
    }

    fun updateFilterCurrencyExtinctDates(dates : FilterDates, selectedContinent : UInt?){
        _currencyUIState.update { currentState ->
            currentState.copy(
                filterCurExtinct = dates
            )
        }
        updateFiltersState()
        if (dates.isValid) {
            filterCurrencies(selectedContinent)
        }
    }

    private fun updateFiltersState(){
        _currencyHasFilterState.update {
            _currencyUIState.value.filterCurrencyTypes != Pair(true,true) ||
                    _currencyUIState.value.filterCurrencyState != Pair(true,true) ||
                    (_currencyUIState.value.filterCurFounded.isValid && (_currencyUIState.value.filterCurFounded.from != null || _currencyUIState.value.filterCurFounded.to != null)) ||
                    (_currencyUIState.value.filterCurExtinct.isValid && (_currencyUIState.value.filterCurExtinct.from != null || _currencyUIState.value.filterCurExtinct.to != null))
        }
    }

    fun updateSettings(showPreference : ShowPreferenceEnum, value : Boolean ){
        when (showPreference){
            ShowPreferenceEnum.SHOW_DATES -> {
                _currencyUIState.value.currenciesTable.showCol(CurrencyFieldStart, value)
                _currencyUIState.value.currenciesTable.showCol(CurrencyFieldEnd, value)
            }
            ShowPreferenceEnum.SHOW_ISSUES -> {
                _currencyUIState.value.currenciesTable.showCol(CurrencyFieldIssues, value)
            }
            ShowPreferenceEnum.SHOW_VALUES -> {
                _currencyUIState.value.currenciesTable.showCol(CurrencyFieldDenominations, value)
            }
            ShowPreferenceEnum.SHOW_NOTES -> {
                _currencyUIState.value.currenciesTable.showCol(CurrencyFieldNotes, value)
            }
            ShowPreferenceEnum.SHOW_VARIANTS -> {
                _currencyUIState.value.currenciesTable.showCol(CurrencyFieldVariants, value)
            }
            ShowPreferenceEnum.SHOW_PRICES -> {
                _currencyUIState.value.currenciesTable.showCol(CurrencyFieldPrice, value)
            }
            else -> Unit
        }
    }

    fun getCurrencyStats() : Map<String, CurrencySummaryStats> {
        return repository.currencySummaryStats
    }

    private fun filterCurrencies(selectedContinent: UInt?) {
        currenciesViewData = repository.getCurrencies(
            selectedContinent,
            _currencyUIState.value.filterCurrencyTypes,
            _currencyUIState.value.filterCurrencyState,
            _currencyUIState.value.filterCurFounded,
            _currencyUIState.value.filterCurExtinct
        )
    }

}