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

/**
 * Data Class with the user Credentials
 */
data class UserCredentials(
    val username: String = "",
    val password: String = ""
)

/**
 * Class that handles saving and retrieving user credentials
 */
class UserCredentialsRepository(
    private val dataStore: DataStore<Preferences>
) {
    private val tag: String = "UserCredentialsRepo"

    //Keys (and value types) used in the data store
    private companion object  {
        val KEY_CREDENTIALS = stringPreferencesKey("credentials")
    }

    /**
     * Get the user credentials flow.
     */
    val userCredentialsFlow: Flow<UserCredentials> = dataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                Log.e(tag, "Error reading credentials.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val credentials = preferences[KEY_CREDENTIALS]?.split(DELIMITER)
            UserCredentials(credentials?.get(0)?:"", credentials?.get(1)?:"")
        }


    suspend fun updateCredentials(username: String, password: String) {
        dataStore.edit { preferences ->
            preferences[KEY_CREDENTIALS] = username + DELIMITER + password
        }
    }
}


