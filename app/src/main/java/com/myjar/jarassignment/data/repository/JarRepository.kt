package com.myjar.jarassignment.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.myjar.jarassignment.data.PagingSourceMovies
import com.myjar.jarassignment.data.api.ApiService
import com.myjar.jarassignment.data.model.ComputerItem
import com.myjar.jarassignment.data.model.MovieDetails
import com.myjar.jarassignment.data.model.Search
import com.myjar.jarassignment.data.moviesapi.MoviesApiInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/*interface JarRepository {
    suspend fun fetchResults(): Flow<List<ComputerItem>>
    fun getMoviesListing(query: String = ""): Flow<PagingData<Search>>
    suspend fun getMoviesDetails(key: String): MovieDetails?
}*/

class JarRepository @Inject constructor(val apiInterface: MoviesApiInterface) {
    suspend fun fetchResults(): Flow<List<ComputerItem>> {
        TODO("Not yet implemented")
    }

     fun getMoviesListing(query:String) = Pager(
        config = PagingConfig(10, maxSize = 100, enablePlaceholders = true),
        pagingSourceFactory = { PagingSourceMovies(apiInterface,searchKey = query) }).flow

     suspend fun getMoviesDetails(key:String):MovieDetails?{
        val url ="https://www.omdbapi.com/?apikey=7513b73b&t=$key"
        return apiInterface.getMoviesDetails(url).body()
    }
}