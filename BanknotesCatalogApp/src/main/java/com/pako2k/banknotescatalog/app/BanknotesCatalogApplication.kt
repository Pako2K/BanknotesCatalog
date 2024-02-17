package com.pako2k.banknotescatalog.app

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.pako2k.banknotescatalog.R
import com.pako2k.banknotescatalog.data.BanknotesCatalogRepository
import com.pako2k.banknotescatalog.data.UserPreferencesRepository
import com.pako2k.banknotescatalog.network.BanknotesAPIClient


private const val USER_PREFERENCES_NAME = "banknotes_user_preferences"

// Local datastore
private val Context.appUserDataStore by preferencesDataStore (
    name = USER_PREFERENCES_NAME
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
        repository = BanknotesCatalogRepository.create(applicationContext, apiClient)

        userPreferencesRepository = UserPreferencesRepository(appUserDataStore)
    }
}