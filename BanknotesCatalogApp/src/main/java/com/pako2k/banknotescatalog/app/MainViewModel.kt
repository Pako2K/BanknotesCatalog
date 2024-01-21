package com.pako2k.banknotescatalog.app

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.network.BanknotesAPIClient
import com.pako2k.banknotescatalog.ui.parts.Sorting
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


// Class implementing the Application Logic
class MainViewModel
    private constructor(
        private val _application: Application
    ) : ViewModel () {

    // Private so it cannot be updated outside this MainViewModel
    private val _mainUiState = MutableStateFlow(MainUiState())
    // Public property to read the UI state
    val uiState = _mainUiState.asStateFlow()

    // Private set so it cannot be updated outside this MainViewModel
    var mainUiData : MainUiData = MainUiData()
        private set


    // ViewModel can only be created by ViewModelProvider.Factory
    companion object {
        val Factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                //val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BanknotesApplication)
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                Log.d(application.getString(R.string.app_log_tag), "Create MainViewModel")
                MainViewModel(application)
            }
        }
    }

    init {
        Log.d(_application.getString(R.string.app_log_tag), "Start INIT MainViewModel")

        val url = _application.getString(R.string.BANKNOTES_API_BASE_URL)
        val timeout = _application.applicationContext.resources.getInteger(R.integer.BANKNOTES_API_TIMEOUT)

        // Create apiClient instance
        val apiClient = BanknotesAPIClient(url, timeout)

        // Initialization in separate coroutines
        val job = viewModelScope.async {
            Log.d(_application.getString(R.string.app_log_tag), "Start asynchronous getContinents")

            // Get Continents and territories
            val result = try {
                mainUiData.continents = apiClient.getContinents()
                mainUiData.territoryTypes = apiClient.getTerritoryTypes()

                ComponentState.DONE
            }
            catch (exc : Exception){
                Log.e(_application.getString(R.string.app_log_tag), exc.toString())
                ComponentState.FAILED
            }

            Log.d(_application.getString(R.string.app_log_tag), "End asynchronous getContinents")

            return@async result
        }
        viewModelScope.launch {
            Log.d(_application.getString(R.string.app_log_tag), "Start asynchronous getTerritories")

            // Get Continents and territories
            var result : ComponentState = try {
                mainUiData.territories = apiClient.getTerritories()
                ComponentState.DONE
            }
            catch (exc : Exception){
                Log.e(_application.getString(R.string.app_log_tag), exc.toString())
                ComponentState.FAILED
            }

            if (job.await() == ComponentState.FAILED) result = ComponentState.FAILED

            // filter territories in non-active continents (e.g. antarctica)
            mainUiData.territories = mainUiData.territories.filter { ter -> mainUiData.continents.find { it.id == ter.continentId } != null }

            _mainUiState.update {currentState ->
                currentState.copy(
                    mainInitialization = result
                )
            }

            Log.d(_application.getString(R.string.app_log_tag), "End asynchronous getTerritories")
        }

        Log.d(_application.getString(R.string.app_log_tag), "End INIT MainViewModel")
    }

    fun setContinent(continentId : UInt) {
        val newSelection =
            if (continentId == uiState.value.selectedContinent) null
            else continentId

        _mainUiState.update { currentState ->
            currentState.copy(
                selectedContinent = newSelection,
            )
        }
    }

    fun sortTerritoriesBy(dataColumn : Int) {
        val newSortingDir =
            if (_mainUiState.value.territoriesSortedBy == dataColumn)
                if (_mainUiState.value.territoriesSortingDir == Sorting.ASC)
                    Sorting.DESC
                else
                    Sorting.ASC
            else
                Sorting.ASC

        _mainUiState.update { currentState ->
            currentState.copy(
                territoriesSortedBy = dataColumn,
                territoriesSortingDir = newSortingDir
            )
        }
    }

}