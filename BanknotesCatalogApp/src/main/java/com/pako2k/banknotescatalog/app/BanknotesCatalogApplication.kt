package com.pako2k.banknotescatalog.app

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.data.repo.BanknotesCatalogRepository
import com.pako2k.banknotescatalog.data.repo.ContinentCacheRepository
import com.pako2k.banknotescatalog.data.repo.ShowPreferencesRepository
import com.pako2k.banknotescatalog.data.repo.TerritoryTypeCacheRepository
import com.pako2k.banknotescatalog.data.repo.UserCredentialsRepository
import com.pako2k.banknotescatalog.data.repo.UserPreferencesRepository
import com.pako2k.banknotescatalog.network.BanknotesAPIClient


private const val CONTINENT_CACHE_NAME = "banknotes_continent_cache"
private const val TER_TYPE_CACHE_NAME = "banknotes_ter_type_cache"
private const val USER_PREFERENCES_NAME = "banknotes_user_preferences"
private const val SHOW_PREFERENCES_NAME = "banknotes_show_preferences"
private const val USER_CREDENTIALS_NAME = "banknotes_user_credentials"

// Local datastore's
private val Context.appUserDataStore by preferencesDataStore (
    name = USER_PREFERENCES_NAME
)
private val Context.appShowDataStore by preferencesDataStore (
    name = SHOW_PREFERENCES_NAME
)
private val Context.appContinentCacheStore by preferencesDataStore (
    name = CONTINENT_CACHE_NAME
)
private val Context.appTerTypeCacheStore by preferencesDataStore (
    name = TER_TYPE_CACHE_NAME
)
private val Context.appUserCredentialsStore by preferencesDataStore (
    name = USER_CREDENTIALS_NAME
)

class BanknotesCatalogApplication : Application() {
    lateinit var userPreferencesRepository : UserPreferencesRepository
    lateinit var showPreferencesRepository : ShowPreferencesRepository
    lateinit var userCredentialsRepository : UserCredentialsRepository
    lateinit var repository : BanknotesCatalogRepository

    override fun onCreate() {
        super.onCreate()
        // Create apiClient instance and Repository
        val baseURL = applicationContext.getString(R.string.BANKNOTES_API_BASE_URL)
        val timeout = applicationContext.resources.getInteger(R.integer.BANKNOTES_API_TIMEOUT)
        val apiClient = BanknotesAPIClient(baseURL, timeout)

        userPreferencesRepository = UserPreferencesRepository(appUserDataStore)
        showPreferencesRepository = ShowPreferencesRepository(appShowDataStore)
        userCredentialsRepository = UserCredentialsRepository(appUserCredentialsStore)
        repository = BanknotesCatalogRepository.create(
            applicationContext,
            apiClient,
            ContinentCacheRepository(appContinentCacheStore),
            TerritoryTypeCacheRepository(appTerTypeCacheStore)
            )
    }
}