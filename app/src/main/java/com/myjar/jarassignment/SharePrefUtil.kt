package com.myjar.jarassignment

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.myjar.jarassignment.data.model.Search
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

@Singleton
class SharedPrefs constructor(context: Context) {

    companion object {
        private const val PREF_NAME = "movies_pref"
        const val FAVOURITES="favourites"
        const val AVENGERS_CACHE_KEY = "avengers_top_10_cache"
    }

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)




    val gson by lazy { Gson() }

    // JSON operations for objects
    fun <T> putObject(key: String, value: T?) {
        value?.let {
            sharedPrefs.edit { putString(key, gson.toJson(it)) }
        } ?: run {
            remove(key)
        }
    }

    fun <T> getObject(key: String, type: Class<T>): T? {
        val json = sharedPrefs.getString(key, null)
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            Log.e("PrefsManager", "Error parsing object for key: $key", e)
            null
        }
    }

    fun <T> putList(key: String, list: List<T>) {
        val json = gson.toJson(list)
        sharedPrefs.edit().putString(key, json).apply()
    }

    fun <T> getList(key: String, type: Class<T>): List<T> {
        val json = sharedPrefs.getString(key, null)
        if (json.isNullOrEmpty()) return emptyList()
        return try {
            val typeToken = TypeToken.getParameterized(List::class.java, type).type
            gson.fromJson<List<T>>(json, typeToken)
        } catch (e: Exception) {
            Log.e("PrefsManager", "Error parsing list for key: $key", e)
            emptyList()
        }
    }

    fun <T> getListFlow(key: String, type: Class<T>): Flow<List<T>> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
            if (changedKey == key) {
                trySend(getList(key, type)).isSuccess
            }
        }

        sharedPrefs.registerOnSharedPreferenceChangeListener(listener)

        // Emit initial value
        trySend(getList(key, type)).isSuccess

        awaitClose {
            sharedPrefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }.flowOn(Dispatchers.IO)

    fun isFavorite(movieId: String): Flow<Boolean> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { sp, key ->
            if (key == FAVOURITES) {
                launch {
                    val currentFavorites = getList(FAVOURITES, Search::class.java).toMutableList()

                    trySend(currentFavorites.any { it.imdbID == movieId }).isSuccess
                }
            }
        }
        sharedPrefs.registerOnSharedPreferenceChangeListener(listener)
        launch {
            val initialFavorites = getList(FAVOURITES, Search::class.java).toMutableList()
            send(initialFavorites.any { it.imdbID == movieId })
        }
        awaitClose { sharedPrefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }.flowOn(Dispatchers.IO)


    fun putString(key: String, value: String) {
        sharedPrefs.edit { putString(key, value) }
    }

    fun getString(key: String, defaultValue: String? = null): String? {
        return sharedPrefs.getString(key, defaultValue)
    }

    fun putBoolean(key: String, value: Boolean) {
        sharedPrefs.edit { putBoolean(key, value) }
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPrefs.getBoolean(key, defaultValue)
    }

    fun putInt(key: String, value: Int) {
        sharedPrefs.edit { putInt(key, value) }
    }

    fun getInt(key: String, defaultValue: Int = 0): Int {
        return sharedPrefs.getInt(key, defaultValue)
    }

    fun putLong(key: String, value: Long) {
        sharedPrefs.edit { putLong(key, value) }
    }

    fun getLong(key: String, defaultValue: Long = 0L): Long {
        return sharedPrefs.getLong(key, defaultValue)
    }

    fun remove(key: String) {
        sharedPrefs.edit { remove(key) }
    }

    fun clearAll() {
        sharedPrefs.edit { clear() }
    }

    fun contains(key: String): Boolean {
        return sharedPrefs.contains(key)
    }
}
