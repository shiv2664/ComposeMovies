package com.myjar.jarassignment.data

import android.content.SharedPreferences
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.myjar.jarassignment.SharedPrefs
import com.myjar.jarassignment.data.model.Search
import com.myjar.jarassignment.data.moviesapi.MoviesApiInterface
import javax.inject.Inject

class PagingSourceMovies @Inject constructor(
    private val apiInterface: MoviesApiInterface,
    private val searchKey: String,
    private val sharedPrefs: SharedPrefs, // Added
    private val onAvengersFirstPageFetched: (List<Search>) -> Unit // Added
) : PagingSource<Int, Search>() {

    private var prevSearchKey = ""
    private val defaultIndex: Int = 1
    private val avengersQuery = "avengers"


/*
    private fun getAvengersCacheFromSp(): List<Search?>? {
        return listOf(sharedPrefs.getObject(SharedPrefs.AVENGERS_CACHE_KEY, Search::class.java))
    }
*/

    private fun getAvengersCacheFromSp(): List<Search> {
        return sharedPrefs.getList(SharedPrefs.AVENGERS_CACHE_KEY, Search::class.java)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Search> {
        var position = defaultIndex
        if (prevSearchKey != searchKey) {
            position = defaultIndex
            prevSearchKey = searchKey
        } else {
            position = params.key ?: defaultIndex
        }

        // Try to load from cache if it's the first page for "avengers"
        if (position == defaultIndex && searchKey.equals(avengersQuery, ignoreCase = true)) {
            val cachedItems = getAvengersCacheFromSp()?:emptyList()
            if (cachedItems.isNotEmpty()) {
                return LoadResult.Page(
                    data = cachedItems,
                    prevKey = null,
                    // If 10 items were cached, next key is 2, else null (no more cached pages)
                    nextKey = if (cachedItems.size == 10) 2 else null
                )
            }
        }

        val response = try {
            apiInterface.getMoviesList("https://www.omdbapi.com/?apikey=7513b73b&s=$searchKey&page=$position").body()
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }

        val movies = response?.Search ?: emptyList()

        // If it's the first page of "avengers" and data was fetched from network, trigger callback
        if (position == defaultIndex && searchKey.equals(avengersQuery, ignoreCase = true) && movies.isNotEmpty()) {
            onAvengersFirstPageFetched(movies) // ViewModel will .take(10) and save
        }

        return LoadResult.Page(
            data = movies,
            prevKey = if (position == defaultIndex) null else position - 1,
            nextKey = if (movies.isNullOrEmpty()) null else position + 1
        )
    }

    override fun getRefreshKey(state: PagingState<Int, Search>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
