package com.example.flexibeat.data.datasave

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.PreferencesProto.StringSet
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.Preferences.Key
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("global_prefs")

object GlobalRepository {
    private lateinit var dataStore: DataStore<Preferences>
    private val keyMap = mutableMapOf<String, Key<*>>()

    fun initialize(context: Context) { dataStore = context.dataStore }

    @Suppress("UNCHECKED_CAST")
    private fun <T> getOrCreateKey(name: String, value: T): Key<T> {
        return keyMap.getOrPut(name) {
            when (value) {
                is Int -> intPreferencesKey(name)
                is Boolean -> booleanPreferencesKey(name)
                is Long -> longPreferencesKey(name)
                is String -> stringPreferencesKey(name)
                is Float -> floatPreferencesKey(name)
                is Double -> doublePreferencesKey(name)
                is ByteArray -> byteArrayPreferencesKey(name)
                is StringSet -> stringSetPreferencesKey(name)
                else -> throw IllegalArgumentException("Unsupported type: ${value?.let { it::class.simpleName } ?: "null" }")
            }
        } as Key<T>
    }

    suspend fun <T> saveValue(name: String, value: T) {
        val key = getOrCreateKey(name, value)
        dataStore.edit { it[key] = value }
    }

    fun <T> getValue(name: String, defaultValue: T): Flow<T> {
        val key = getOrCreateKey(name, defaultValue)
        return dataStore.data.map { it[key] ?: defaultValue }
    }
}
