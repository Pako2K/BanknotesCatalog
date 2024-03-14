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
import com.pako2k.banknotescatalog.data.repo.DenomFieldCurrencies
import com.pako2k.banknotescatalog.data.repo.DenomFieldNotes
import com.pako2k.banknotescatalog.data.repo.DenomFieldPrice
import com.pako2k.banknotescatalog.data.repo.DenomFieldTerritories
import com.pako2k.banknotescatalog.data.repo.DenomFieldVariants
import com.pako2k.banknotescatalog.data.repo.DenomSortableField
import com.pako2k.banknotescatalog.data.repo.ShowPreferenceEnum
import com.pako2k.banknotescatalog.data.stats.DenomTotalStats
import com.pako2k.banknotescatalog.data.stats.DenominationSummaryStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.DecimalFormat


class DenominationViewModel private constructor(
    ctx: Context,
    private val repository : BanknotesCatalogRepository,
) : ViewModel() {

    // Private so it cannot be updated outside this MainViewModel
    private val _denominationUIState = MutableStateFlow(DenominationUIState())
    // Public property to read the UI state
    val denominationUIState = _denominationUIState.asStateFlow()

    // Private so it cannot be updated outside this MainViewModel
    private val _denominationHasFilterState = MutableStateFlow(false)
    // Public property to read the UI state
    val denominationHasFilterState = _denominationHasFilterState.asStateFlow()

    lateinit var denominationsViewDataUI : List<List<Any?>>
        private set
    private fun setDenominationsViewDataUI(selectedContinent: UInt?) {
        val uiData : MutableList<List<Any?>> = mutableListOf()
        var stats : DenomTotalStats
        for(den in repository.denominationCatStats){
            stats = if (selectedContinent != null){
                val denCont = den.continentStats.find { it.id == selectedContinent } ?: continue
                DenomTotalStats(
                    denCont.isCurrent,
                    denCont.numTerritories,
                    denCont.numCurrencies,
                    denCont.numNotes,
                    denCont.numVariants
                )
            } else {
                den.totalStats
            }

            uiData.add(
                listOf(
                    DecimalFormat("#,###.###").format(den.denomination),
                    stats.numTerritories.toString(),
                    "-",
                    stats.numCurrencies.toString(),
                    "-",
                    stats.numNotes.toString(),
                    "-",
                    stats.numVariants.toString(),
                    "-",
                    "-"
                )
            )
        }
        denominationsViewDataUI = uiData
    }

    // ViewModel can only be created by ViewModelProvider.Factory
    companion object {
        val Factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BanknotesCatalogApplication
                Log.d(application.getString(R.string.app_log_tag), "Create DenominationViewModel")

                DenominationViewModel(
                    application.applicationContext,
                    application.repository,
                )
            }
        }
    }

    init {
        Log.d(ctx.getString(R.string.app_log_tag), "Start INIT DenominationViewModel")

        repository.setDenominationStats()
        // At this point the repository is already initialized
        setDenominationsViewDataUI(null)
    }

    fun setContinentFilter(selectedContinentId : UInt?) {
        repository.setDenominationStats(selectedContinentId)

        // Re-Sorting is needed
        val sortedCol = _denominationUIState.value.denominationsTable.columns[_denominationUIState.value.denominationsTable.sortedBy]
        repository.sortDenominations(sortedCol.linkedField as DenomSortableField,
            sortedCol.sortedStats,
            sortedCol.sortedDirection!!,
            selectedContinentId)

        setDenominationsViewDataUI(selectedContinentId)

        _denominationUIState.update { currentState ->
            currentState.copy(
                denominationsTableUpdateTrigger = !currentState.denominationsTableUpdateTrigger
            )
        }
    }

    fun sortDenominationsBy(sortBy : DenomSortableField, statsCol : StatsSubColumn?, selectedContinent : UInt?) {
        _denominationUIState.value.denominationsTable.sortBy(_denominationUIState.value.denominationsTable.getCol(sortBy)?:0, statsCol)
        val sortedColumn = _denominationUIState.value.denominationsTable.sortedBy
        val newSortingDir = _denominationUIState.value.denominationsTable.columns[sortedColumn].sortedDirection!!

        repository.sortDenominations(sortBy, statsCol, newSortingDir, selectedContinent)

        setDenominationsViewDataUI(selectedContinent)

        _denominationUIState.update { currentState ->
            currentState.copy(
                denominationsTableUpdateTrigger = !currentState.denominationsTableUpdateTrigger
            )
        }
    }

    fun getDenominationStats() : DenominationSummaryStats = repository.denominationSummaryStats


    fun updateFilterIssueYearDates(dates : FilterDates, selectedContinent: UInt?){

        _denominationUIState.update { currentState ->
            currentState.copy(
                filterShownIssueYear =  dates,
                state = if (dates.isValid) ComponentState.LOADING else currentState.state
            )
        }

        if (!dates.isValid) return

        viewModelScope.launch {
            try {
                repository.fetchDenominationStats(
                    dates.from,
                    dates.to
                )
                repository.setDenominationStats(selectedContinent)
                setDenominationsViewDataUI(selectedContinent)
                _denominationUIState.update { currentState ->
                    currentState.copy(
                        filterAppliedIssueYear = dates,
                        state = ComponentState.DONE
                    )
                }
                _denominationHasFilterState.update {
                    dates.from != null || dates.to!= null
                }
            } catch (exc: Exception) {
                Log.e("App-BanknotesCatalog", "Fetch Denominations: " + exc.toString() + " - " + exc.cause)
                _denominationUIState.update { currentState ->
                    currentState.copy(
                        state = ComponentState.DONE
                    )
                }
            }
        }
    }

    fun updateSettings(showPreference : ShowPreferenceEnum, value : Boolean ){
        when (showPreference){
            ShowPreferenceEnum.SHOW_TERRITORIES -> {
                _denominationUIState.value.denominationsTable.showCol(DenomFieldTerritories, value)
            }
            ShowPreferenceEnum.SHOW_CUR -> {
                _denominationUIState.value.denominationsTable.showCol(DenomFieldCurrencies, value)
            }
            ShowPreferenceEnum.SHOW_NOTES -> {
                _denominationUIState.value.denominationsTable.showCol(DenomFieldNotes, value)
            }
            ShowPreferenceEnum.SHOW_VARIANTS -> {
                _denominationUIState.value.denominationsTable.showCol(DenomFieldVariants, value)
            }
            ShowPreferenceEnum.SHOW_PRICES -> {
                _denominationUIState.value.denominationsTable.showCol(DenomFieldPrice, value)
            }
            else -> Unit
        }
    }

    fun showStats(visible: Boolean){
        _denominationUIState.update { currentState ->
            currentState.copy(
                showStats = visible,
                showFilters = false
            )
        }
    }

    fun showFilters(visible: Boolean){
        _denominationUIState.update { currentState ->
            currentState.copy(
                showFilters = visible,
                showStats = false
            )
        }
    }

}