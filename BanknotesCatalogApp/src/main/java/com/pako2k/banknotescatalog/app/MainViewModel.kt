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
import com.pako2k.banknotescatalog.data.CurrencySortableField
import com.pako2k.banknotescatalog.data.TerritorySortableField
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

    var territoriesData : List<Map<String,Any?>>
        private set

    var currenciesData : List<Map<String,Any?>>
        private set



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
        territoriesData = listOf()
        currenciesData = listOf()


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

                territoriesData = repository.getTerritoriesData(null)
                ComponentState.DONE
            }
            catch (exc : Exception){
                Log.e(ctx.getString(R.string.app_log_tag), exc.toString())
                ComponentState.FAILED
            }

            Log.d(ctx.getString(R.string.app_log_tag), "End asynchronous getTerritories")
            return@async result
        }

        val job3 = viewModelScope.async {
            Log.d(ctx.getString(R.string.app_log_tag), "Start asynchronous getCurrencies")

            // Get Currencies
            val result : ComponentState = try {
                repository.fetchCurrencies()

                currenciesData = repository.getCurrenciesData(null)
                ComponentState.DONE
            }
            catch (exc : Exception){
                Log.e(ctx.getString(R.string.app_log_tag), exc.toString())
                ComponentState.FAILED
            }

            Log.d(ctx.getString(R.string.app_log_tag), "End asynchronous getCurrencies")
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

        territoriesData = repository.getTerritoriesData(newSelection)
        currenciesData = repository.getCurrenciesData(newSelection)

        _mainUiState.update { currentState ->
            currentState.copy(
                selectedContinent = newSelection,
            )
        }
    }

    fun sortTerritoriesBy(sortBy : TerritorySortableField) {

        _mainUiState.value.territoriesTable.sortBy( _mainUiState.value.territoriesTable.getCol(sortBy)?:2)

        val sortedColumn = _mainUiState.value.territoriesTable.sortedBy
        val newSortingDir = _mainUiState.value.territoriesTable.columns[sortedColumn].sortedDirection!!
        repository.sortTerritories(sortBy, newSortingDir )

        territoriesData = repository.getTerritoriesData(_mainUiState.value.selectedContinent)

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

        currenciesData = repository.getCurrenciesData(_mainUiState.value.selectedContinent)

        _mainUiState.update { currentState ->
            currentState.copy(
                summaryTableSortingFlag = !currentState.summaryTableSortingFlag
            )
        }
    }

}