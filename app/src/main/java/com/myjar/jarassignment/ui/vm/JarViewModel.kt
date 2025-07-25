package com.myjar.jarassignment.ui.vm
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.myjar.jarassignment.data.model.ComputerItem
import com.myjar.jarassignment.data.model.MovieDetails
import com.myjar.jarassignment.data.model.Search
import com.myjar.jarassignment.data.repository.JarRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JarViewModel @Inject constructor(val repository: JarRepositoryImpl) : ViewModel() {

    private val _listStringData = MutableStateFlow<List<ComputerItem>>(emptyList())
    val listStringData: StateFlow<List<ComputerItem>>
        get() = _listStringData


    fun getMoviesListing(searchKey: String): Flow<PagingData<Search>> {
        return repository.getMoviesListing(searchKey).cachedIn(viewModelScope)
    }

    suspend fun getDetails(title: String): MovieDetails? {
        return repository.getMoviesDetails(title)
    }

    fun fetchData() {
        viewModelScope.launch {
            repository.fetchResults().collect { result ->
                    _listStringData.value = result
                }
        }
    }
}