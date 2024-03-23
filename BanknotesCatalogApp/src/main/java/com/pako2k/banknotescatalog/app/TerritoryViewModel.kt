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
import com.pako2k.banknotescatalog.data.Territory
import com.pako2k.banknotescatalog.data.TerritoryTypeEnum
import com.pako2k.banknotescatalog.data.repo.BanknotesCatalogRepository
import com.pako2k.banknotescatalog.data.repo.ShowPreferenceEnum
import com.pako2k.banknotescatalog.data.repo.ShowPreferencesRepository
import com.pako2k.banknotescatalog.data.repo.TerritoryFieldCurrencies
import com.pako2k.banknotescatalog.data.repo.TerritoryFieldDenominations
import com.pako2k.banknotescatalog.data.repo.TerritoryFieldEnd
import com.pako2k.banknotescatalog.data.repo.TerritoryFieldIssues
import com.pako2k.banknotescatalog.data.repo.TerritoryFieldNotes
import com.pako2k.banknotescatalog.data.repo.TerritoryFieldPrice
import com.pako2k.banknotescatalog.data.repo.TerritoryFieldStart
import com.pako2k.banknotescatalog.data.repo.TerritoryFieldVariants
import com.pako2k.banknotescatalog.data.repo.TerritorySortableField
import com.pako2k.banknotescatalog.data.stats.TerritorySummaryStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class TerritoryViewModel private constructor(
    ctx: Context,
    private val repository : BanknotesCatalogRepository,
    private val showPreferencesRepository: ShowPreferencesRepository
) : ViewModel() {
    private var LOG_TAG = ctx.getString(R.string.app_log_tag)

    // Private so it cannot be updated outside this MainViewModel
    private val _territoryUIState = MutableStateFlow(TerritoryUIState())
    // Public property to read the UI state
    val territoryUIState = _territoryUIState.asStateFlow()

    // Private so it cannot be updated outside this MainViewModel
    private val _territoryHasFilterState = MutableStateFlow(false)
    // Public property to read the UI state
    val territoryHasFilterState = _territoryHasFilterState.asStateFlow()

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


    // ViewModel can only be created by ViewModelProvider.Factory
    companion object {
        val Factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BanknotesCatalogApplication
                Log.d(application.getString(R.string.app_log_tag), "Create TerritoryViewModel")

                TerritoryViewModel(
                    application.applicationContext,
                    application.repository,
                    application.showPreferencesRepository
                )
            }
        }
    }

    init {
        Log.d(LOG_TAG, "Start INIT territoryViewModel")

        viewModelScope.launch {
            showPreferencesRepository.showPreferencesFlow.first().let { preferences ->
                preferences.map.forEach {
                    if (!it.value) updateSettings(it.key, false)
                }
            }
        }

        // At this point the repository is already initialized
        territoriesViewData = repository.territories
        repository.setTerritoryStats(null)
    }

    fun setContinentFilter(selectedContinentId : UInt?) {
        filterTerritories(selectedContinentId)
        repository.setCurrencyStats(selectedContinentId)

        _territoryUIState.update { currentState ->
            currentState.copy(
                summaryTableTriggerUpdateFlag = !currentState.summaryTableTriggerUpdateFlag
            )
        }
    }

    fun sortTerritoriesBy(sortBy : TerritorySortableField, statsCol : StatsSubColumn?, selectedContinent: UInt?) {
        _territoryUIState.value.territoriesTable.sortBy( _territoryUIState.value.territoriesTable.getCol(sortBy)?:2, statsCol)

        val sortedColumn = _territoryUIState.value.territoriesTable.sortedBy
        val newSortingDir = _territoryUIState.value.territoriesTable.columns[sortedColumn].sortedDirection!!
        repository.sortTerritories(sortBy, statsCol, newSortingDir )

        // Apply filter to the entire sorted list
        filterTerritories(selectedContinent)

        _territoryUIState.update { currentState ->
            currentState.copy(
                summaryTableTriggerUpdateFlag = !currentState.summaryTableTriggerUpdateFlag
            )
        }
    }

    fun updateSettings(showPreference : ShowPreferenceEnum, value : Boolean ){
        when (showPreference){
            ShowPreferenceEnum.SHOW_DATES -> {
                _territoryUIState.value.territoriesTable.showCol(TerritoryFieldStart, value)
                _territoryUIState.value.territoriesTable.showCol(TerritoryFieldEnd, value)
            }
            ShowPreferenceEnum.SHOW_CUR -> {
                _territoryUIState.value.territoriesTable.showCol(TerritoryFieldCurrencies, value)
            }
            ShowPreferenceEnum.SHOW_ISSUES -> {
                _territoryUIState.value.territoriesTable.showCol(TerritoryFieldIssues, value)
            }
            ShowPreferenceEnum.SHOW_VALUES -> {
                _territoryUIState.value.territoriesTable.showCol(TerritoryFieldDenominations, value)
            }
            ShowPreferenceEnum.SHOW_NOTES -> {
                _territoryUIState.value.territoriesTable.showCol(TerritoryFieldNotes, value)
            }
            ShowPreferenceEnum.SHOW_VARIANTS -> {
                _territoryUIState.value.territoriesTable.showCol(TerritoryFieldVariants, value)
            }
            ShowPreferenceEnum.SHOW_PRICES -> {
                _territoryUIState.value.territoriesTable.showCol(TerritoryFieldPrice, value)
            }
            else -> Unit
        }
    }

    fun updateFilterTerritoryType(type : TerritoryTypeEnum, isSelected : Boolean, selectedContinent : UInt?){
        val newMap = _territoryUIState.value.filterTerritoryTypes.mapValues { if(it.key == type) isSelected else it.value }

        // At least one type must be selected!!
        if (!newMap.containsValue(true)) return

        _territoryUIState.update { currentState ->
            currentState.copy(
                filterTerritoryTypes = newMap
            )
        }
        updateFiltersState()
        filterTerritories(selectedContinent)
    }

    fun updateFilterTerritoryState(isSelected : Pair<Boolean, Boolean>, selectedContinent : UInt?){
        // At least one state must be true!!
        if (!isSelected.first && !isSelected.second) return

        _territoryUIState.update { currentState ->
            currentState.copy(
                filterTerritoryState = isSelected
            )
        }
        updateFiltersState()
        filterTerritories(selectedContinent)
    }

    fun updateFilterTerritoryFoundedDates(dates : FilterDates, selectedContinent : UInt?){
        _territoryUIState.update { currentState ->
            currentState.copy(
                filterTerFounded = dates
            )
        }
        updateFiltersState()
        if (dates.isValid) {
            filterTerritories(selectedContinent)
        }
    }

    fun updateFilterTerritoryExtinctDates(dates : FilterDates, selectedContinent : UInt?){
        _territoryUIState.update { currentState ->
            currentState.copy(
                filterTerExtinct = dates
            )
        }
        updateFiltersState()
        if (dates.isValid) {
            filterTerritories(selectedContinent)
        }
    }

    fun showTerritoryStats(visible: Boolean){
        _territoryUIState.update { currentState ->
            currentState.copy(
                showTerritoryStats = visible,
                showTerritoryFilters = false
            )
        }
    }


    fun showTerritoryFilters(visible: Boolean){
        _territoryUIState.update { currentState ->
            currentState.copy(
                showTerritoryFilters = visible,
                showTerritoryStats = false
            )
        }
    }

    fun getTerritoryStats() : Map<String, TerritorySummaryStats> {
        return repository.territorySummaryStats
    }

    private fun updateFiltersState(){
        _territoryHasFilterState.update {
            _territoryUIState.value.filterTerritoryState != Pair(true, true) ||
            _territoryUIState.value.filterTerritoryTypes.containsValue(false) ||
            (_territoryUIState.value.filterTerFounded.isValid && (_territoryUIState.value.filterTerFounded.from != null || _territoryUIState.value.filterTerFounded.to != null)) ||
            (_territoryUIState.value.filterTerExtinct.isValid && (_territoryUIState.value.filterTerExtinct.from != null || _territoryUIState.value.filterTerExtinct.to != null))
        }
    }

    private fun filterTerritories(selectedContinent: UInt?) {
        val filterTerTypesToList = _territoryUIState.value.filterTerritoryTypes.filter { it.value }.keys.toList().let {
            if (it.size == _territoryUIState.value.filterTerritoryTypes.size) null else it
        }
        territoriesViewData = repository.getTerritories(
            selectedContinent,
            filterTerTypesToList,
            _territoryUIState.value.filterTerritoryState.first,
            _territoryUIState.value.filterTerritoryState.second,
            _territoryUIState.value.filterTerFounded,
            _territoryUIState.value.filterTerExtinct
        )
    }
}