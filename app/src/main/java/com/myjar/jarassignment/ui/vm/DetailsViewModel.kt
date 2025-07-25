package com.myjar.jarassignment.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.myjar.jarassignment.NetworkResult
import com.myjar.jarassignment.data.model.MovieDetails
import com.myjar.jarassignment.data.repository.JarRepository
import com.myjar.jarassignment.data.repository.JarRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(val repository: JarRepositoryImpl): ViewModel() {

    private val _details = MutableStateFlow<NetworkResult<MovieDetails?>>(NetworkResult.Loading(""))
    val details: StateFlow<NetworkResult<MovieDetails?>> = _details

    fun getDetails(title: String) {
        viewModelScope.launch {
            repository.getMoviesDetails(title).collect{result ->
                _details.value=result

            }

        }
    }
}