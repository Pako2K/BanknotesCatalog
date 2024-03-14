package com.pako2k.banknotescatalog.data.repo

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val DELIMITER = ","
private const val HISTORY_DEPTH = 5


/**
 * Data Class with the user preferences
 */
data class UserPreferences(
    val favouriteTerritories : List<UInt> = listOf(),
    val favouriteCurrencies : List<UInt> = listOf(),
    val historyTerritories : List<UInt> = listOf(),
    val historyCurrencies : List<UInt> = listOf(),
)


/**
 * Class that handles saving and retrieving user preferences
 */
class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private val tag: String = "UserPreferencesRepo"

    //Keys (and value types) used in the data store
    private companion object  {
        val KEY_FAV_TER = stringPreferencesKey("fav_ter")
        val KEY_FAV_CUR = stringPreferencesKey("fav_cur")
        val KEY_HIST_TER = stringPreferencesKey("hist_ter")
        val KEY_HIST_CUR = stringPreferencesKey("hist_cur")
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
        updateFavouritePref(id, KEY_FAV_TER, KEY_HIST_TER)
    }

    suspend fun updateFavCur(id: UInt) {
        updateFavouritePref(id, KEY_FAV_CUR, KEY_HIST_CUR)
    }

    suspend fun updateHistTer(id: UInt) {
        updateHistoryPref(id, KEY_HIST_TER, KEY_FAV_TER)
    }

    suspend fun updateHistCur(id: UInt) {
        updateHistoryPref(id, KEY_HIST_CUR, KEY_FAV_CUR)
    }

    private suspend fun updateFavouritePref(id : UInt, favKey : Preferences.Key<String>, histKey: Preferences.Key<String> ){
        dataStore.edit { preferences ->
            val newList = preferences[favKey]?.split(DELIMITER)?.map { it.toUInt() }?.toMutableList() ?: mutableListOf()
            if (!newList.remove(id)) newList.add(id)
            val strValue = newList.joinToString(separator = DELIMITER)
            if (strValue.isNotEmpty())
                preferences[favKey] = strValue
            else
                preferences.remove(favKey)

            // Remove from history
            val newHistList = preferences[histKey]?.split(DELIMITER)?.map { it.toUInt() }?.toMutableList() ?: mutableListOf()
            newHistList.remove(id)
            val strValue2 = newHistList.joinToString(separator = DELIMITER)
            if (strValue2.isNotEmpty())
                preferences[histKey] = strValue2
            else
                preferences.remove(histKey)
        }
    }

    private suspend fun updateHistoryPref(id : UInt, histKey : Preferences.Key<String>, favKey: Preferences.Key<String> ){
        dataStore.edit { preferences ->
            val newList = preferences[histKey]?.split(DELIMITER)?.map { it.toUInt() }?.toMutableList() ?: mutableListOf()

            // Add id if not there yet, and not in the favourites list.
            val favList = preferences[favKey]?.split(DELIMITER)?.map { it.toUInt() }?.toMutableList() ?: mutableListOf()
            if (!newList.contains(id) && !favList.contains(id)) newList.add(id)

            // Remove first element if list is bigger than HISTORY_DEPTH
            if (newList.size > HISTORY_DEPTH) newList.removeAt(0)

            val strValue = newList.joinToString(separator = DELIMITER)
            if (strValue.isNotEmpty())
                preferences[histKey] = strValue
            else
                preferences.remove(histKey)
        }
    }

    private fun mapUserPreferences(preferences: Preferences): UserPreferences {
        val favouriteTerritories = preferences[KEY_FAV_TER]?.split(DELIMITER)?.map { it.toUInt() }?: listOf()
        val favouriteCurrencies = preferences[KEY_FAV_CUR]?.split(DELIMITER)?.map { it.toUInt() }?: listOf()
        val historyTerritories = preferences[KEY_HIST_TER]?.split(DELIMITER)?.map { it.toUInt() }?: listOf()
        val historyCurrencies = preferences[KEY_HIST_CUR]?.split(DELIMITER)?.map { it.toUInt() }?: listOf()

        return UserPreferences(favouriteTerritories, favouriteCurrencies, historyTerritories, historyCurrencies)
    }
}


