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
import com.pako2k.banknotescatalog.data.Territory
import com.pako2k.banknotescatalog.data.repo.BanknotesCatalogRepository
import com.pako2k.banknotescatalog.data.repo.ShowPreferences
import com.pako2k.banknotescatalog.data.repo.ShowPreferencesRepository
import com.pako2k.banknotescatalog.data.repo.UserPreferences
import com.pako2k.banknotescatalog.data.repo.UserPreferencesRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


private const val MAX_RETRIES = 3
private const val RETRY_INTERVAL = 1000L


// Class implementing the Application Logic
class MainViewModel private constructor(
    ctx: Context,
    private val repository : BanknotesCatalogRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    showPreferencesRepository: ShowPreferencesRepository
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


    fun territoryViewData(terId : UInt) : Territory? {
        val ter = (repository.territories.find{ it.id == terId })
        ter?.extend(territoriesList = repository.territories, flags = repository.flags)
        return ter
    }

    fun currencyViewData(curId : UInt) : Currency? {
        val cur = (repository.currencies.find{ it.id == curId })
        cur?.extend(territoriesList = repository.territories, flags = repository.flags, currenciesList = repository.currencies)
        return cur
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
                    retryCount = MAX_RETRIES
                    ComponentState.DONE
                } catch (exc: Exception) {
                    Log.e(ctx.getString(R.string.app_log_tag), exc.toString() + " - " + exc.cause)
                    retryCount++
                    delay(RETRY_INTERVAL)
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
                    delay(RETRY_INTERVAL)
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
                    retryCount = MAX_RETRIES
                    ComponentState.DONE
                } catch (exc: Exception) {
                    Log.e(ctx.getString(R.string.app_log_tag), exc.toString() + " - " + exc.cause)
                    retryCount++
                    delay(RETRY_INTERVAL)
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
                    delay(RETRY_INTERVAL)
                    ComponentState.FAILED
                }
            }

            Log.d(ctx.getString(R.string.app_log_tag), "End asynchronous getCurrencyStats with $result")
            return@async result
        }.let { jobs.add(it) }

        viewModelScope.async {
            Log.d(ctx.getString(R.string.app_log_tag), "Start asynchronous getDenominationStats")

            var retryCount = 0
            var result = ComponentState.LOADING
            while (retryCount < MAX_RETRIES) {
                result = try {
                    repository.fetchDenominationStats()
                    retryCount = MAX_RETRIES
                    ComponentState.DONE
                } catch (exc: Exception) {
                    Log.e(ctx.getString(R.string.app_log_tag), exc.toString() + " - " + exc.cause)
                    retryCount++
                    delay(RETRY_INTERVAL)
                    ComponentState.FAILED
                }
            }

            Log.d(ctx.getString(R.string.app_log_tag), "End asynchronous getDenominationStats with $result")
            return@async result
        }.let { jobs.add(it) }

        viewModelScope.async {
            Log.d(ctx.getString(R.string.app_log_tag), "Start asynchronous getIssueYearStats")

            var retryCount = 0
            var result = ComponentState.LOADING
            while (retryCount < MAX_RETRIES) {
                result = try {
                    repository.fetchIssueYearStats()
                    retryCount = MAX_RETRIES
                    ComponentState.DONE
                } catch (exc: Exception) {
                    Log.e(ctx.getString(R.string.app_log_tag), exc.toString() + " - " + exc.cause)
                    retryCount++
                    delay(RETRY_INTERVAL)
                    ComponentState.FAILED
                }
            }

            Log.d(ctx.getString(R.string.app_log_tag), "End asynchronous getIssueYearStats with $result")
            return@async result
        }.let { jobs.add(it) }


        viewModelScope.launch{
            Log.d(ctx.getString(R.string.app_log_tag), "Waiting for all jobs...")
            val results = jobs.awaitAll()
            Log.d(ctx.getString(R.string.app_log_tag), "Jobs finished with result: $results")
            val finalResult =
                if (results.contains(ComponentState.FAILED))
                    ComponentState.FAILED
                else {
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


    fun userLogged() {
        _mainUiState.update { currentState ->
            currentState.copy(
                userLoggedIn = true
            )
        }
    }

}