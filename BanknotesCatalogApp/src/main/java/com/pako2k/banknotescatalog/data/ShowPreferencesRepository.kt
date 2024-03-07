package com.pako2k.banknotescatalog.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException


/**
 * Data Class with the user preferences
 */
data class ShowPreferences(
    val showDates : Boolean = true,
    val showCurrencies : Boolean = true,
    val showIssues : Boolean = true,
    val showFaceValues : Boolean = true,
    val showNoteTypes : Boolean = true,
    val showVariants : Boolean = true,
    val showPrice: Boolean = true
)

enum class ShowPreferenceEnum(val key : String){
    KEY_SHOW_DATES(key = "show_dates"),
    KEY_SHOW_CUR(key = "show_cur"),
    KEY_SHOW_ISSUES(key = "show_issues"),
    KEY_SHOW_VALUES(key = "show_values"),
    KEY_SHOW_NOTES(key = "show_notes"),
    KEY_SHOW_VARIANTS(key = "show_variants"),
    KEY_SHOW_PRICES(key = "show_prices")
}


/**
 * Class that handles saving and retrieving user preferences
 */
class ShowPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private val tag: String = "ShowPreferencesRepo"

    //Keys (and value types) used in the data store
    private fun ShowPreferenceEnum.boolKey() = booleanPreferencesKey(this.key)


    /**
     * Get the user preferences flow.
     */
    val showPreferencesFlow: Flow<ShowPreferences> = dataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                Log.e(tag, "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            ShowPreferences(
                preferences[ShowPreferenceEnum.KEY_SHOW_DATES.boolKey()]?:true,
                preferences[ShowPreferenceEnum.KEY_SHOW_CUR.boolKey()]?:true,
                preferences[ShowPreferenceEnum.KEY_SHOW_ISSUES.boolKey()]?:true,
                preferences[ShowPreferenceEnum.KEY_SHOW_VALUES.boolKey()]?:true,
                preferences[ShowPreferenceEnum.KEY_SHOW_NOTES.boolKey()]?:true,
                preferences[ShowPreferenceEnum.KEY_SHOW_VARIANTS.boolKey()]?:true,
                preferences[ShowPreferenceEnum.KEY_SHOW_PRICES.boolKey()]?:true
            )
        }

    suspend fun updateShowPreference(showOption : ShowPreferenceEnum, value: Boolean) {
        dataStore.edit { preferences ->
            preferences[showOption.boolKey()] = value
        }
    }
}


