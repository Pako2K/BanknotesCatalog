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
import com.pako2k.banknotescatalog.data.Territory
import com.pako2k.banknotescatalog.network.BanknotesAPIClient
import com.pako2k.banknotescatalog.ui.parts.Sorting
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


// Class implementing the Application Logic
class MainViewModel private constructor(
        application: Application,
    ) : ViewModel () {

    // Private so it cannot be updated outside this MainViewModel
    private val _mainUiState = MutableStateFlow(MainUiState())
    // Public property to read the UI state
    val uiState = _mainUiState.asStateFlow()

    // Private set so it cannot be updated outside this MainViewModel
    var repository : BanknotesCatalogRepository
        private set

    var territoriesData : List<Map<String,Any?>>
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

        // Initialize flags
        viewModelScope.launch {
            repository.fetchFlags()
        }

        // Initialization in separate coroutines
        val job = viewModelScope.async {
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
        viewModelScope.launch {
            Log.d(ctx.getString(R.string.app_log_tag), "Start asynchronous getTerritories")

            // Get Territories
            var result : ComponentState = try {
                repository.fetchTerritories()

                territoriesData = repository.getTerritoriesData()
                ComponentState.DONE
            }
            catch (exc : Exception){
                Log.e(ctx.getString(R.string.app_log_tag), exc.toString())
                ComponentState.FAILED
            }

            if (job.await() == ComponentState.FAILED) result = ComponentState.FAILED

            _mainUiState.update {currentState ->
                currentState.copy(
                    mainInitialization = result
                )
            }

            Log.d(ctx.getString(R.string.app_log_tag), "End asynchronous getTerritories")
        }

        Log.d(ctx.getString(R.string.app_log_tag), "End INIT MainViewModel")
    }



    fun setContinentFilter(continentId : UInt) {
        val newSelection =
            if (continentId == uiState.value.selectedContinent) null
            else continentId

        territoriesData = repository.getTerritoriesDataByContinent(newSelection)

        _mainUiState.update { currentState ->
            currentState.copy(
                selectedContinent = newSelection,
            )
        }
    }

    fun sortTerritoriesBy(sortBy : Territory.SortableCol) {
        val newSortingDir =
            if (_mainUiState.value.territoriesSortedBy == sortBy)
                if (_mainUiState.value.territoriesSortingDir == Sorting.ASC)
                    Sorting.DESC
                else
                    Sorting.ASC
            else
                Sorting.ASC

        repository.sortTerritories(sortBy, newSortingDir)

        territoriesData = repository.getTerritoriesDataByContinent(_mainUiState.value.selectedContinent)

        _mainUiState.update { currentState ->
            currentState.copy(
                territoriesSortedBy = sortBy,
                territoriesSortingDir = newSortingDir
            )
        }
    }

}