package com.myjar.jarassignment.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.myjar.jarassignment.NetworkResult
import com.myjar.jarassignment.SharedPrefs
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

class JarRepositoryImpl @Inject constructor(
    private val apiInterface: MoviesApiInterface,
    private val sharedPrefs: SharedPrefs
) : JarRepository {

    override fun fetchResults(): Flow<List<ComputerItem>> {
        TODO("Not yet implemented")
    }

    override fun getMoviesListing(
        query: String,
        onAvengersFirstPageFetched: (List<Search>) -> Unit
    ) = Pager(
        config = PagingConfig(pageSize = 10, maxSize = 100, enablePlaceholders = true),
        pagingSourceFactory = {
            PagingSourceMovies(
                apiInterface = apiInterface,
                searchKey = query,
                sharedPrefs = sharedPrefs,
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

    private suspend fun getFavoritesListFromSp(): MutableList<Search?> {
        return withContext(Dispatchers.IO) {

//            val search=sharedPrefs.getObject(SharedPrefs.FAVOURITES, Search::class.java)
            sharedPrefs.getList(SharedPrefs.FAVOURITES, Search::class.java).toMutableList()

        }
    }

    private suspend fun saveFavoritesListToSp(favorites: List<Search?>) {
        withContext(Dispatchers.IO) {
            sharedPrefs.putList(SharedPrefs.FAVOURITES, favorites)
        }
    }

    override suspend fun addFavorite(movie: Search) {
        val favorites = getFavoritesListFromSp()
        if (favorites.none { it?.imdbID == movie.imdbID }) {
            favorites.add(movie)
            saveFavoritesListToSp(favorites)
        } else {
            removeFavorite(movie.imdbID)
        }
    }

    override suspend fun removeFavorite(movieId: String) {
        val favorites = getFavoritesListFromSp()
        val updatedFavorites = favorites.filterNot { it?.imdbID == movieId }
        saveFavoritesListToSp(updatedFavorites)
    }

    override fun getFavoriteMovies(): Flow<List<Search>> =
        sharedPrefs.getListFlow(SharedPrefs.FAVOURITES, Search::class.java)


    override fun isFavorite(movieId: String): Flow<Boolean> = sharedPrefs.isFavorite(movieId)

}
