package com.pako2k.banknotescatalog.app

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.data.FilterDates
import com.pako2k.banknotescatalog.data.repo.BanknotesCatalogRepository
import com.pako2k.banknotescatalog.data.repo.IssueYearFieldCurrencies
import com.pako2k.banknotescatalog.data.repo.IssueYearFieldIssues
import com.pako2k.banknotescatalog.data.repo.IssueYearFieldNotes
import com.pako2k.banknotescatalog.data.repo.IssueYearFieldPrice
import com.pako2k.banknotescatalog.data.repo.IssueYearFieldTerritories
import com.pako2k.banknotescatalog.data.repo.IssueYearFieldValue
import com.pako2k.banknotescatalog.data.repo.IssueYearFieldVariants
import com.pako2k.banknotescatalog.data.repo.IssueYearSortableField
import com.pako2k.banknotescatalog.data.repo.ShowPreferenceEnum
import com.pako2k.banknotescatalog.data.repo.ShowPreferencesRepository
import com.pako2k.banknotescatalog.data.stats.IssueYearSummaryStats
import com.pako2k.banknotescatalog.data.stats.IssueYearTotalStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class IssueYearViewModel private constructor(
    ctx: Context,
    private val repository : BanknotesCatalogRepository,
    private val showPreferencesRepository: ShowPreferencesRepository
) : ViewModel() {

    // Private so it cannot be updated outside this MainViewModel
    private val _issueYearUIState = MutableStateFlow(IssueYearUIState())
    // Public property to read the UI state
    val issueYearUIState = _issueYearUIState.asStateFlow()

    // Private so it cannot be updated outside this MainViewModel
    private val _issueYearHasFilterState = MutableStateFlow(false)
    // Public property to read the UI state
    val issueYearHasFilterState = _issueYearHasFilterState.asStateFlow()

    lateinit var issueYearsViewDataUI : List<List<Any?>>
        private set

    var issueYearSummaryStats : IssueYearSummaryStats = IssueYearSummaryStats(IssueYearSummaryStats.Data(0,0))
        private set

    private fun setIssueYearsViewDataUI(selectedContinent: UInt?) {
        val uiData : MutableList<List<Any?>> = mutableListOf()
        var stats : IssueYearTotalStats?
        for(year in repository.issueYearCatStats){
            stats = null
            if (selectedContinent != null){
                year.continentStats.find { it.id == selectedContinent }?.let {
                    stats = IssueYearTotalStats(
                        it.numTerritories,
                        it.numCurrencies,
                        it.numSeries,
                        it.numDenominations,
                        it.numNotes,
                        it.numVariants
                    )
                }
            }
            else
                stats = year.totalStats

            if ((stats != null) && ((_issueYearUIState.value.filterAppliedIssueYear.from?: 0) <= year.issueYear)
                && ((_issueYearUIState.value.filterAppliedIssueYear.to ?: 10000) >= year.issueYear)
            )
                uiData.add(
                    listOf(
                        year.issueYear,
                        stats!!.numTerritories.toString(),
                        "-",
                        stats!!.numCurrencies.toString(),
                        "-",
                        stats!!.numSeries.toString(),
                        "-",
                        stats!!.numDenominations.toString(),
                        "-",
                        stats!!.numNotes.toString(),
                        "-",
                        stats!!.numVariants.toString(),
                        "-",
                        "-"
                    )
                )
        }
        issueYearsViewDataUI = uiData
        issueYearSummaryStats = IssueYearSummaryStats(IssueYearSummaryStats.Data(uiData.size,0))
    }

    // ViewModel can only be created by ViewModelProvider.Factory
    companion object {
        val Factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BanknotesCatalogApplication
                Log.d(application.getString(R.string.app_log_tag), "Create IssueYearViewModel")

                IssueYearViewModel(
                    application.applicationContext,
                    application.repository,
                    application.showPreferencesRepository
                )
            }
        }
    }

    init {
        Log.d(ctx.getString(R.string.app_log_tag), "Start INIT IssueYearViewModel")

        viewModelScope.launch {
            showPreferencesRepository.showPreferencesFlow.first().let { preferences ->
                preferences.map.forEach {
                    if (!it.value) updateSettings(it.key, false)
                }
            }
        }

        // At this point the repository is already initialized
        setIssueYearsViewDataUI(null)
    }

    fun setContinentFilter(selectedContinentId : UInt?) {
        repository.setDenominationStats(selectedContinentId)

        // Re-Sorting is needed for issue years
        val sortedCol2 = _issueYearUIState.value.yearsTable.columns[_issueYearUIState.value.yearsTable.sortedBy]
        repository.sortIssueYears(sortedCol2.linkedField as IssueYearSortableField,
            sortedCol2.sortedStats,
            sortedCol2.sortedDirection!!,
            selectedContinentId)

        setIssueYearsViewDataUI(selectedContinentId)

        _issueYearUIState.update { currentState ->
            currentState.copy(
                yearsTableUpdateTrigger = !currentState.yearsTableUpdateTrigger
            )
        }
    }


    fun sortIssueYearsBy(sortBy : IssueYearSortableField, statsCol : StatsSubColumn?, selectedContinent : UInt?) {
        _issueYearUIState.value.yearsTable.sortBy(_issueYearUIState.value.yearsTable.getCol(sortBy)?:0, statsCol)
        val sortedColumn = _issueYearUIState.value.yearsTable.sortedBy
        val newSortingDir = _issueYearUIState.value.yearsTable.columns[sortedColumn].sortedDirection!!

        repository.sortIssueYears(sortBy, statsCol, newSortingDir, selectedContinent)

        setIssueYearsViewDataUI(selectedContinent)

        _issueYearUIState.update { currentState ->
            currentState.copy(
                yearsTableUpdateTrigger = !currentState.yearsTableUpdateTrigger
            )
        }

    }


    fun updateFilterIssueYearDates(dates : FilterDates, selectedContinent: UInt?){
        _issueYearUIState.update { currentState ->
            currentState.copy(
                filterShownIssueYear = dates
            )
        }
        if (dates.isValid) {
            _issueYearUIState.update { currentState ->
                currentState.copy(
                    filterAppliedIssueYear = dates
                )
            }
            _issueYearHasFilterState.update {
                dates.from != null || dates.to!= null
            }
            setIssueYearsViewDataUI(selectedContinent)
        }
    }


    fun updateSettings(showPreference : ShowPreferenceEnum, value : Boolean ){
        when (showPreference){
            ShowPreferenceEnum.SHOW_TERRITORIES -> {
                _issueYearUIState.value.yearsTable.showCol(IssueYearFieldTerritories, value)
            }
            ShowPreferenceEnum.SHOW_CUR -> {
                _issueYearUIState.value.yearsTable.showCol(IssueYearFieldCurrencies, value)
            }
            ShowPreferenceEnum.SHOW_ISSUES-> {
                _issueYearUIState.value.yearsTable.showCol(IssueYearFieldIssues, value)
            }
            ShowPreferenceEnum.SHOW_VALUES -> {
                _issueYearUIState.value.yearsTable.showCol(IssueYearFieldValue, value)
            }
            ShowPreferenceEnum.SHOW_NOTES -> {
                _issueYearUIState.value.yearsTable.showCol(IssueYearFieldNotes, value)
            }
            ShowPreferenceEnum.SHOW_VARIANTS -> {
                _issueYearUIState.value.yearsTable.showCol(IssueYearFieldVariants, value)
            }
            ShowPreferenceEnum.SHOW_PRICES -> {
                _issueYearUIState.value.yearsTable.showCol(IssueYearFieldPrice, value)
            }
            else -> Unit
        }
    }

    fun showFilters(visible: Boolean){
        _issueYearUIState.update { currentState ->
            currentState.copy(
                showIssueYearFilters = visible,
                showIssueYearStats = false
            )
        }
    }

    fun showStats(visible: Boolean){
        _issueYearUIState.update { currentState ->
            currentState.copy(
                showIssueYearStats = visible,
                showIssueYearFilters = false
            )
        }
    }

}