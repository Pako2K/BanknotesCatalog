package com.pako2k.banknotescatalog.app

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.pako2k.banknotescatalog.data.UserPreferencesRepository


private const val USER_PREFERENCES_NAME = "banknotes_user_preferences"

// Local datastore
val Context.appUserDataStore by preferencesDataStore (
    name = USER_PREFERENCES_NAME
)

class BanknotesCatalogApplication : Application() {
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate() {
        super.onCreate()
        userPreferencesRepository = UserPreferencesRepository(appUserDataStore)
    }
}