package com.myjar.jarassignment.ui.vm

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.Gson
import com.myjar.jarassignment.data.model.ComputerItem
import com.myjar.jarassignment.data.model.Search
import com.myjar.jarassignment.data.repository.JarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first // Added import for .first()
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: JarRepository,
    private val application: Application
) : ViewModel() {

    private val _listStringData = MutableStateFlow<List<ComputerItem>>(emptyList())
    val listStringData: StateFlow<List<ComputerItem>>
        get() = _listStringData

    private val gson = Gson()
    val sharedPreferences: SharedPreferences by lazy {
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

    fun getMoviesListing(searchKey: String): Flow<PagingData<Search>> {
        return repository.getMoviesListing(
            query = searchKey,
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

    // --- Favorite Movies Logic ---
    val favoriteMovies: StateFlow<List<Search>> = repository.getFavoriteMovies(gson)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addFavoriteMovie(movie: Search) {
        viewModelScope.launch {
            repository.addFavorite(movie, gson)
        }
    }

    fun removeFavoriteMovie(movieId: String) {
        viewModelScope.launch {
            repository.removeFavorite(movieId, gson)
        }
    }

    fun isFavorite(movieId: String): Flow<Boolean> {
        return repository.isFavorite(movieId, gson)
    }

    fun toggleFavorite(movie: Search) {
        viewModelScope.launch {
            val isCurrentlyFavorite = isFavorite(movie.imdbID).first() // Corrected: Use .first() for single emission
            if (isCurrentlyFavorite) {
                removeFavoriteMovie(movie.imdbID) // Use ViewModel's method
            } else {
                addFavoriteMovie(movie) // Use ViewModel's method
            }
        }
    }
}
