package com.myjar.jarassignment.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit // For SharedPreferences.edit extension
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.myjar.jarassignment.NetworkResult
import com.myjar.jarassignment.data.PagingSourceMovies
import com.myjar.jarassignment.data.model.ComputerItem
import com.myjar.jarassignment.data.model.MovieDetails
import com.myjar.jarassignment.data.model.Search
import com.myjar.jarassignment.data.moviesapi.MoviesApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface JarRepository {
    fun fetchResults(): Flow<List<ComputerItem>>
    fun getMoviesListing(
        query: String = "",
        gson: Gson,
        onAvengersFirstPageFetched: (List<Search>) -> Unit
    ): Flow<PagingData<Search>>

    fun getMoviesDetails(key: String): Flow<NetworkResult<MovieDetails?>>

    // Favorite movie functions
    suspend fun addFavorite(movie: Search, gson: Gson)
    suspend fun removeFavorite(movieId: String, gson: Gson)
    fun getFavoriteMovies(gson: Gson): Flow<List<Search>>
    fun isFavorite(movieId: String,gson: Gson): Flow<Boolean>
}

class JarRepositoryImpl @Inject constructor(
    private val apiInterface: MoviesApiInterface,
    private val sharedPreferences: SharedPreferences
) : JarRepository {

    private val favoritesKey = "favorite_movies_list" // Key for storing favorites in SharedPreferences

    override fun fetchResults(): Flow<List<ComputerItem>> {
        TODO("Not yet implemented")
    }

    override fun getMoviesListing(
        query: String,
        gson: Gson,
        onAvengersFirstPageFetched: (List<Search>) -> Unit
    ) = Pager(
        config = PagingConfig(pageSize = 10, maxSize = 100, enablePlaceholders = true),
        pagingSourceFactory = {
            PagingSourceMovies(
                apiInterface = apiInterface,
                searchKey = query,
                sharedPreferences = sharedPreferences,
                gson = gson,
                onAvengersFirstPageFetched = onAvengersFirstPageFetched
            )
        }
    ).flow

    override fun getMoviesDetails(key: String): Flow<NetworkResult<MovieDetails?>> = flow {
        val url = "https://www.omdbapi.com/?apikey=7513b73b&t=$key"
        val response = apiInterface.getMoviesDetails(url)
        if (response.isSuccessful) {
            // Explicitly specify type for NetworkResult.Success
            emit(NetworkResult.Success<MovieDetails?>(response.body()))
        } else {
            emit(NetworkResult.Error(response.message().toString()))
        }
    }.flowOn(Dispatchers.IO)

    private suspend fun getFavoritesListFromSp(gson: Gson): MutableList<Search> {
        return withContext(Dispatchers.IO) {
            val json = sharedPreferences.getString(favoritesKey, null)
            if (json != null) {
                try {
                    val type = object : TypeToken<MutableList<Search>>() {}.type
                    gson.fromJson<MutableList<Search>>(json, type) ?: mutableListOf()
                } catch (e: Exception) {
                    // Log error or handle corrupted cache
                    mutableListOf()
                }
            } else {
                mutableListOf()
            }
        }
    }

    private suspend fun saveFavoritesListToSp(favorites: List<Search>, gson: Gson) {
        withContext(Dispatchers.IO) {
            val json = gson.toJson(favorites)
            sharedPreferences.edit { putString(favoritesKey, json) }
        }
    }

    override suspend fun addFavorite(movie: Search,gson: Gson) {
        val favorites = getFavoritesListFromSp( gson)
        if (favorites.none { it.imdbID == movie.imdbID }) {
            favorites.add(movie)
            saveFavoritesListToSp(favorites, gson)
        }
    }

    override suspend fun removeFavorite(movieId: String, gson: Gson) {
        val favorites = getFavoritesListFromSp( gson)
        val updatedFavorites = favorites.filterNot { it.imdbID == movieId }
        saveFavoritesListToSp(updatedFavorites, gson)
    }

    override fun getFavoriteMovies(gson: Gson): Flow<List<Search>> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { sp, key ->
            if (key == favoritesKey) {
                val json = sp.getString(favoritesKey, null)
                val newFavorites: List<Search> = if (json != null) {
                    try {
                        val type = object : TypeToken<List<Search>>() {}.type
                        gson.fromJson<List<Search>>(json, type) ?: emptyList()
                    } catch (e: Exception) {
                        emptyList()
                    }
                } else {
                    emptyList()
                }
                trySend(newFavorites).isSuccess // Check if send was successful
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        // Emit initial value using send (suspend function)
        launch { // Launch coroutine for suspend function call
             send(getFavoritesListFromSp(gson))
        }
        awaitClose { sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener) }
    }.flowOn(Dispatchers.IO)

    override fun isFavorite(movieId: String, gson: Gson): Flow<Boolean> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { sp, key ->
            if (key == favoritesKey) {
                // Launch a coroutine to call suspend function getFavoritesListFromSp
                // and then process its result to send to the flow.
                launch {
                    val currentFavorites = getFavoritesListFromSp(gson)
                    trySend(currentFavorites.any { it.imdbID == movieId }).isSuccess
                }
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        // Emit initial value
        // Similarly, launch a coroutine for the suspend call for the initial value
        launch {
            val initialFavorites = getFavoritesListFromSp( gson)
            send(initialFavorites.any { it.imdbID == movieId })
        }
        awaitClose { sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener) }
    }.flowOn(Dispatchers.IO)
}
