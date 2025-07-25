package com.myjar.jarassignment.data


import androidx.paging.PagingState
import com.myjar.jarassignment.data.model.Search
import com.myjar.jarassignment.data.moviesapi.MoviesApiInterface
import kotlin.collections.isNullOrEmpty
import kotlin.let

class PagingSourceMovies(
    val apiInterface: MoviesApiInterface,
    val searchKey: String
) : androidx.paging.PagingSource<Int,Search>() {
    private var prevSearchKey = ""
    private val defaultIndex: Int = 1

    override suspend fun load(params: LoadParams<Int>):LoadResult<Int,Search> {
        // Determine the position to load

        var position = defaultIndex
        if (prevSearchKey != searchKey) {
            position = defaultIndex
            prevSearchKey = searchKey
        } else {
            position = params.key ?: defaultIndex
        }

        val response = try {
            apiInterface.getMoviesList("https://www.omdbapi.com/?apikey=7513b73b&s=$searchKey&page=$position").body()
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }

        return LoadResult.Page(
            data = response?.Search ?: emptyList(),
            prevKey = if (position == defaultIndex) null else position - 1,
            nextKey = if (response?.Search.isNullOrEmpty()) null else position + 1
        )
    }

    override fun getRefreshKey(state: PagingState<Int, Search>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}


//class PagingSourceMovies @Inject constructor(
//    private val apiInterface: ApiInterface,
//    val searchKey: String
//) : PagingSource<Int, Search>() {
//    private var prevSearchKey = ""
//    private val defaultIndex: Int = 1
//
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Search> {
//        // Determine the position to load
//
//        val position = params.key ?: defaultIndex
//
////        var position = defaultIndex
////        if (prevSearchKey != searchKey) {
////            position = defaultIndex
////            prevSearchKey = searchKey
////        } else {
////            position = params.key ?: defaultIndex
////        }
//
//        Log.d("MyTag", "Loading data for page: ${params.key ?: defaultIndex} with searchKey: $searchKey")
//        // Make the API call
//        val response = try { apiInterface.getMoviesList("http://www.omdbapi.com/?apikey=7513b73b&s=$searchKey&page=$position").body()
//        } catch (e: Exception) {
//            return LoadResult.Error(e)
//        }
//        Log.d("PagingSourceMovies", "Response data size: ${response?.Search?.size ?: 0}")
//        return LoadResult.Page(
//            data = response?.Search ?: emptyList(),
//            prevKey = if (position == defaultIndex) null else position - 1,
//            nextKey = if (response?.Search.isNullOrEmpty()) null else position + 1
//        )
//    }
//
//    override fun getRefreshKey(state: PagingState<Int, Search>): Int? {
//        return state.anchorPosition?.let { anchorPosition ->
//            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
//                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
//        }
//    }
//}