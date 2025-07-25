package com.myjar.jarassignment.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.myjar.jarassignment.NetworkResult
import com.myjar.jarassignment.data.PagingSourceMovies
import com.myjar.jarassignment.data.model.ComputerItem
import com.myjar.jarassignment.data.model.MovieDetails
import com.myjar.jarassignment.data.model.Search
import com.myjar.jarassignment.data.moviesapi.MoviesApiInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface JarRepository {
    fun fetchResults(): Flow<List<ComputerItem>>
    fun getMoviesListing(query: String = ""): Flow<PagingData<Search>>
    fun getMoviesDetails(key: String): Flow<NetworkResult<MovieDetails?>>
}

class JarRepositoryImpl @Inject constructor(val apiInterface: MoviesApiInterface) : JarRepository {
    override fun fetchResults(): Flow<List<ComputerItem>> {
        TODO("Not yet implemented")
    }

    override fun getMoviesListing(query: String) = Pager(
        config = PagingConfig(10, maxSize = 100, enablePlaceholders = true),
        pagingSourceFactory = { PagingSourceMovies(apiInterface, searchKey = query) }).flow

    override fun getMoviesDetails(key: String): Flow<NetworkResult<MovieDetails?>> = flow {
        val url = "https://www.omdbapi.com/?apikey=7513b73b&t=$key"
        val response = apiInterface.getMoviesDetails(url)
        if (response.isSuccessful) {
            emit(NetworkResult.Success(response.body()))
        } else {
            emit(NetworkResult.Error(response.message().toString()))

        }
    }
}