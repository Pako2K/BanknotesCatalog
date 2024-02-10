package com.pako2k.banknotescatalog.app

import android.app.Application
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
import com.pako2k.banknotescatalog.data.TerritoryTypes
import com.pako2k.banknotescatalog.network.BanknotesAPIClient
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


// Class implementing the Application Logic
class MainViewModel private constructor(
        application: Application,
    ) : ViewModel() {

    // Private so it cannot be updated outside this MainViewModel
    private val _mainUiState = MutableStateFlow(MainUiState())
    // Public property to read the UI state
    val uiState = _mainUiState.asStateFlow()

    // Private so it cannot be updated outside this MainViewModel
    private val _mainUiInitializationState = MutableStateFlow(MainUiInitializationState())
    // Public property to read the UI state
    val initializationState = _mainUiInitializationState.asStateFlow()

    // Private set so it cannot be updated outside this MainViewModel
    private val repository : BanknotesCatalogRepository

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
            uiData.add(
                listOf(
                    repository.flags[ter.flagName],
                    ter.iso3?:"",
                    Pair(ter.id, ter.name + terTypSuffix),
                    ter.start.toString(),
                    ter.end?.toString()?:""
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
        return repository.currencies.find{ it.id == curId }
    }

    // ViewModel can only be created by ViewModelProvider.Factory
    companion object {
        val Factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                Log.d(application.getString(R.string.app_log_tag), "Create MainViewModel")
                MainViewModel(application)
            }
        }
    }

    init {
        val ctx = application.applicationContext

        Log.d(ctx.getString(R.string.app_log_tag), "Start INIT MainViewModel")

        val url = ctx.getString(R.string.BANKNOTES_API_BASE_URL)
        val timeout = ctx.resources.getInteger(R.integer.BANKNOTES_API_TIMEOUT)

        // Create apiClient instance
        val apiClient = BanknotesAPIClient(url, timeout)

        repository = BanknotesCatalogRepository.create(ctx, apiClient)
        territoriesViewData = listOf()
        currenciesViewData = listOf()


        // Initialization in separate coroutines
        val job1 = viewModelScope.async {
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
        }
        val job2 = viewModelScope.async {
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

            Log.d(ctx.getString(R.string.app_log_tag), "End asynchronous getTerritories with ${result.toString()}")
            return@async result
        }

        val job3 = viewModelScope.async {
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

            Log.d(ctx.getString(R.string.app_log_tag), "End asynchronous getCurrencies with ${result.toString()}")
            return@async result
        }

        viewModelScope.launch{
            val results = awaitAll(job1, job2, job3)
            val finalResult = if (results.contains(ComponentState.FAILED)) ComponentState.FAILED
            else ComponentState.DONE

            _mainUiInitializationState.update {currentState ->
                currentState.copy(
                    state = finalResult
                )
            }
        }

        Log.d(ctx.getString(R.string.app_log_tag), "End INIT MainViewModel")
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

        _mainUiState.update { currentState ->
            currentState.copy(
                selectedContinent = newSelection
            )
        }
    }

    fun sortTerritoriesBy(sortBy : TerritorySortableField) {

        _mainUiState.value.territoriesTable.sortBy( _mainUiState.value.territoriesTable.getCol(sortBy)?:2)

        val sortedColumn = _mainUiState.value.territoriesTable.sortedBy
        val newSortingDir = _mainUiState.value.territoriesTable.columns[sortedColumn].sortedDirection!!
        repository.sortTerritories(sortBy, newSortingDir )


        territoriesViewData = _mainUiState.value.selectedContinent?.let {repository.getTerritories(it)}?:repository.territories

        _mainUiState.update { currentState ->
            currentState.copy(
                summaryTableSortingFlag = !currentState.summaryTableSortingFlag
            )
        }
    }

    fun sortCurrenciesBy(sortBy : CurrencySortableField) {
        _mainUiState.value.currenciesTable.sortBy(_mainUiState.value.currenciesTable.getCol(sortBy)?:1)
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

}