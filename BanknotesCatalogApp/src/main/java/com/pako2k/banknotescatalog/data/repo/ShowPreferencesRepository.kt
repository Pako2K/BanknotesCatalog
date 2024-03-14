package com.pako2k.banknotescatalog.data.repo

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
    val showTerritories : Boolean = true,
    val showCurrencies : Boolean = true,
    val showIssues : Boolean = true,
    val showFaceValues : Boolean = true,
    val showNoteTypes : Boolean = true,
    val showVariants : Boolean = true,
    val showPrice: Boolean = true
){
    val map : Map<ShowPreferenceEnum, Boolean> = mapOf(
        ShowPreferenceEnum.SHOW_DATES to showDates,
        ShowPreferenceEnum.SHOW_TERRITORIES to showTerritories,
        ShowPreferenceEnum.SHOW_CUR to showCurrencies,
        ShowPreferenceEnum.SHOW_ISSUES to showIssues,
        ShowPreferenceEnum.SHOW_VALUES to showFaceValues,
        ShowPreferenceEnum.SHOW_NOTES to showNoteTypes,
        ShowPreferenceEnum.SHOW_VARIANTS to showVariants,
        ShowPreferenceEnum.SHOW_PRICES to showPrice
    )
}

enum class ShowPreferenceEnum(val key : String){
    SHOW_DATES(key = "show_dates"),
    SHOW_TERRITORIES(key = "show_territories"),
    SHOW_CUR(key = "show_cur"),
    SHOW_ISSUES(key = "show_issues"),
    SHOW_VALUES(key = "show_values"),
    SHOW_NOTES(key = "show_notes"),
    SHOW_VARIANTS(key = "show_variants"),
    SHOW_PRICES(key = "show_prices")
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
                preferences[ShowPreferenceEnum.SHOW_DATES.boolKey()]?:true,
                preferences[ShowPreferenceEnum.SHOW_TERRITORIES.boolKey()]?:true,
                preferences[ShowPreferenceEnum.SHOW_CUR.boolKey()]?:true,
                preferences[ShowPreferenceEnum.SHOW_ISSUES.boolKey()]?:true,
                preferences[ShowPreferenceEnum.SHOW_VALUES.boolKey()]?:true,
                preferences[ShowPreferenceEnum.SHOW_NOTES.boolKey()]?:true,
                preferences[ShowPreferenceEnum.SHOW_VARIANTS.boolKey()]?:true,
                preferences[ShowPreferenceEnum.SHOW_PRICES.boolKey()]?:true
            )
        }

    suspend fun updateShowPreference(showOption : ShowPreferenceEnum, value: Boolean) {
        dataStore.edit { preferences ->
            preferences[showOption.boolKey()] = value
        }
    }
}


