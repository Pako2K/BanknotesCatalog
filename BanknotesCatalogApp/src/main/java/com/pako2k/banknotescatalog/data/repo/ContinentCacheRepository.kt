package com.pako2k.banknotescatalog.data.repo

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.pako2k.banknotescatalog.data.Continent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException


/**
 * Data Class with the user preferences
 */

typealias ContinentCache  = List<Continent>

/**
 * Class that handles saving and retrieving user preferences
 */
class ContinentCacheRepository(
    private val dataStore: DataStore<Preferences>
) {
    private val tag: String = "DataCacheRepo"

    /**
     * Get the user preferences flow.
     */
    val continentCacheFlow: Flow<ContinentCache> = dataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                Log.e(tag, "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences.asMap().map { entry -> Continent(entry.key.name.toUInt(), entry.value.toString()) }
        }

    suspend fun updateContinents(continents: List<Continent>) {
        dataStore.edit { preferences ->
            continents.forEach{ cont ->
                val key = stringPreferencesKey(cont.id.toString())
                preferences[key] = cont.name
            }
        }
    }
}


