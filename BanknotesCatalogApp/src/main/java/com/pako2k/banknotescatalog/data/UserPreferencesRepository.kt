package com.pako2k.banknotescatalog.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val DELIMITER = ","
private const val HISTORY_DEPTH = 5


/**
 * Data Class with the user preferences
 */
data class UserPreferences(
    val favouriteTerritories : List<UInt>,
    val favouriteCurrencies : List<UInt>,
    val historyTerritories : List<UInt>,
    val historyCurrencies : List<UInt>,
)


/**
 * Class that handles saving and retrieving user preferences
 */
class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private val tag: String = "UserPreferencesRepo"

    //Keys (and value types) used in the data store
    private object PreferencesKeys {
        val FAV_TER = stringPreferencesKey("fav_ter")
        val FAV_CUR = stringPreferencesKey("fav_cur")
        val HIST_TER = stringPreferencesKey("hist_ter")
        val HIST_CUR = stringPreferencesKey("hist_cur")
    }


    /**
     * Get the user preferences flow.
     */
    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                Log.e(tag, "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            mapUserPreferences(preferences)
        }


    suspend fun updateFavTer(id: UInt) {
        dataStore.edit { preferences ->
            val newList = preferences[PreferencesKeys.FAV_TER]?.split(DELIMITER)?.map { it.toUInt() }?.toMutableList() ?: mutableListOf()
            if (!newList.remove(id)) newList.add(id)
            val strValue = newList.joinToString(separator = DELIMITER)
            if (strValue.isNotEmpty())
                preferences[PreferencesKeys.FAV_TER] = strValue
            else
                preferences.remove(PreferencesKeys.FAV_TER)
        }
    }

    suspend fun updateFavCur(id: UInt) {

    }

    suspend fun updateHistTer(id: UInt) {
        dataStore.edit { preferences ->
            val newList = preferences[PreferencesKeys.HIST_TER]?.split(DELIMITER)?.map { it.toUInt() }?.toMutableList() ?: mutableListOf()

            // Add id if not there yet, and not in the favourites list.
            val favList = preferences[PreferencesKeys.FAV_TER]?.split(DELIMITER)?.map { it.toUInt() }?.toMutableList() ?: mutableListOf()
            if (!newList.contains(id) && !favList.contains(id)) newList.add(id)

            // Remove first element if list is bigger than HISTORY_DEPTH
            if (newList.size > HISTORY_DEPTH) newList.removeAt(0)

            val strValue = newList.joinToString(separator = DELIMITER)
            if (strValue.isNotEmpty())
                preferences[PreferencesKeys.HIST_TER] = strValue
            else
                preferences.remove(PreferencesKeys.HIST_TER)
        }

        val newList = userPreferencesFlow.first().historyTerritories.toMutableList()
        if (!newList.contains(id)) {
            newList.add(id)
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.HIST_TER] = newList.joinToString(DELIMITER)
            }
        }
    }

    suspend fun updateHistCur(id: UInt) {
       
    }

    private fun mapUserPreferences(preferences: Preferences): UserPreferences {
        val favouriteTerritories = preferences[PreferencesKeys.FAV_TER]?.split(DELIMITER)?.map { it.toUInt() }?: listOf()
        val favouriteCurrencies = preferences[PreferencesKeys.FAV_CUR]?.split(DELIMITER)?.map { it.toUInt() }?: listOf()
        val historyTerritories = preferences[PreferencesKeys.HIST_TER]?.split(DELIMITER)?.map { it.toUInt() }?: listOf()
        val historyCurrencies = preferences[PreferencesKeys.HIST_CUR]?.split(DELIMITER)?.map { it.toUInt() }?: listOf()

        return UserPreferences(favouriteTerritories, favouriteCurrencies, historyTerritories, historyCurrencies)
    }
}


