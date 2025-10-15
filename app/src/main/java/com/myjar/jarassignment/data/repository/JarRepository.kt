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