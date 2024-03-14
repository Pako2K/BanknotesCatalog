package com.pako2k.banknotescatalog.data.repo

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.pako2k.banknotescatalog.data.TerritoryType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException


/**
 * Data Class with the user preferences
 */

typealias TerritoryTypeCache  = List<TerritoryType>
private const val DELIMITER = ","

/**
 * Class that handles saving and retrieving user preferences
 */
class TerritoryTypeCacheRepository(
    private val dataStore: DataStore<Preferences>
) {
    private val tag: String = "DataCacheRepo"

    /**
     * Get the user preferences flow.
     */
    val territoryTypeCacheFlow: Flow<TerritoryTypeCache> = dataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                Log.e(tag, "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences.asMap().map { entry ->
                val fields = entry.value.toString().split(DELIMITER)
                TerritoryType(id = entry.key.name.toUInt(), abbreviation = fields[0], name = fields[1], description = fields[2])
            }
        }

    suspend fun updateTerritoryTypes(terTypes: TerritoryTypeCache) {
        dataStore.edit { preferences ->
            terTypes.forEach{ type ->
                val key = stringPreferencesKey(type.id.toString())
                preferences[key] = "${type.abbreviation}$DELIMITER${type.name}$DELIMITER${type.description}"
            }
        }
    }
}


