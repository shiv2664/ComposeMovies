package com.myjar.jarassignment.ui.vm

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.myjar.jarassignment.data.model.ComputerItem
import com.myjar.jarassignment.data.model.Search
import com.myjar.jarassignment.data.repository.JarRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: JarRepositoryImpl,
    private val application: Application // Added Application context
) : ViewModel() {

    private val _listStringData = MutableStateFlow<List<ComputerItem>>(emptyList())
    val listStringData: StateFlow<List<ComputerItem>>
        get() = _listStringData

    private val gson = Gson()
    private val sharedPreferences: SharedPreferences by lazy {
        application.getSharedPreferences("movies_cache", Context.MODE_PRIVATE)
    }
    private val avengersCacheKey = "avengers_top_10_cache"
    private val avengersQuery = "avengers"

    private fun saveAvengersCache(items: List<Search>) {
        if (items.isNotEmpty()) {
            val firstTen = items.take(10)
            val json = gson.toJson(firstTen)
            sharedPreferences.edit().putString(avengersCacheKey, json).apply()
        }
    }

    // This function is not used by PagingSource directly but shown for completeness
    // PagingSource will use its own internal getter.
    /*
    private fun getAvengersCache(): List<Search>? {
        val json = sharedPreferences.getString(avengersCacheKey, null)
        return if (json != null) {
            val type = object : TypeToken<List<Search>>() {}.type
            gson.fromJson(json, type)
        } else {
            null
        }
    }
    */

    fun getMoviesListing(searchKey: String): Flow<PagingData<Search>> {
        // JarRepositoryImpl.getMoviesListing needs to be updated to accept these:
        // sharedPreferences, gson, and the onAvengersFirstPageFetched callback.
        return repository.getMoviesListing(
            query = searchKey,
            sharedPreferences = sharedPreferences,
            gson = gson,
            onAvengersFirstPageFetched = { fetchedItems ->
                if (searchKey.equals(avengersQuery, ignoreCase = true)) {
                    saveAvengersCache(fetchedItems)
                }
            }
        ).cachedIn(viewModelScope)
    }


    fun fetchData() {
        viewModelScope.launch {
            repository.fetchResults().collect { result ->
                _listStringData.value = result
            }
        }
    }
}
