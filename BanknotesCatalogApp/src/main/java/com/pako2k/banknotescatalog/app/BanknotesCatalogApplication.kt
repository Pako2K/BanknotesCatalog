package com.pako2k.banknotescatalog.app

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.data.BanknotesCatalogRepository
import com.pako2k.banknotescatalog.data.ContinentCacheRepository
import com.pako2k.banknotescatalog.data.TerritoryTypeCacheRepository
import com.pako2k.banknotescatalog.data.UserPreferencesRepository
import com.pako2k.banknotescatalog.network.BanknotesAPIClient


private const val CONTINENT_CACHE_NAME = "banknotes_continent_cache"
private const val TER_TYPE_CACHE_NAME = "banknotes_ter_type_cache"
private const val USER_PREFERENCES_NAME = "banknotes_user_preferences"

// Local datastore's
private val Context.appUserDataStore by preferencesDataStore (
    name = USER_PREFERENCES_NAME
)
private val Context.appContinentCacheStore by preferencesDataStore (
    name = CONTINENT_CACHE_NAME
)
private val Context.appTerTypeCacheStore by preferencesDataStore (
    name = TER_TYPE_CACHE_NAME
)

class BanknotesCatalogApplication : Application() {
    lateinit var userPreferencesRepository: UserPreferencesRepository
    lateinit var repository : BanknotesCatalogRepository

    override fun onCreate() {
        super.onCreate()
        // Create apiClient instance and Repository
        val baseURL = applicationContext.getString(R.string.BANKNOTES_API_BASE_URL)
        val timeout = applicationContext.resources.getInteger(R.integer.BANKNOTES_API_TIMEOUT)
        val apiClient = BanknotesAPIClient(baseURL, timeout)

        userPreferencesRepository = UserPreferencesRepository(appUserDataStore)
        repository = BanknotesCatalogRepository.create(
            applicationContext,
            apiClient,
            ContinentCacheRepository(appContinentCacheStore),
            TerritoryTypeCacheRepository(appTerTypeCacheStore)
            )
    }
}