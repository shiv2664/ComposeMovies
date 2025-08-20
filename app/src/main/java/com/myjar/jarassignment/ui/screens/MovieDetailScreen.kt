package com.myjar.jarassignment.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.myjar.jarassignment.NetworkResult
import com.myjar.jarassignment.ui.vm.DetailsViewModel

@Composable
fun ItemDetailScreen(title: String?) {

    /* Wrong method no sepration of concern or state management

     val viewModel = hiltViewModel<DetailsViewModel>()
     var result by remember { mutableStateOf<String?>(null) }
     LaunchedEffect(title) {
         result = when (val response = viewModel.getDetails(title.toString())) {
             is NetworkResult.onSuccess -> response.data?.Title ?: "No data"
             is NetworkResult.onError -> "Error: ${response.message ?: "Unknown error"}"
             is NetworkResult.onLoading -> "Loading..."
             else -> "Unknown state"
         }

     }*/

    val viewModel = hiltViewModel<DetailsViewModel>()
    var result by remember { mutableStateOf<String?>("") }
    val detailState by viewModel.details.collectAsState()
    LaunchedEffect(title) {
        viewModel.getDetails(title.toString())
    }

    when (detailState) {
        is NetworkResult.Loading -> {
            result = detailState.message ?: ""
        }

        is NetworkResult.Success -> {
            result = detailState.data?.Title ?: ""
        }

        is NetworkResult.Error -> {
        }

    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (result?.isNotEmpty() == true) {
            Text(
                text = "Item Details for ID: $result",
                modifier = Modifier
                    .padding(16.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )

        }
    }

}