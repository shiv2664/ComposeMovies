package com.myjar.jarassignment.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.myjar.jarassignment.data.model.Search
import com.myjar.jarassignment.ui.composables.ScalableVerticalGrid
import com.myjar.jarassignment.ui.vm.MainViewModel

@Composable
fun MovieListScreen(
    pagingItems: LazyPagingItems<Search>,
    onNavigateToDetail: (String, Search) -> Unit,
    onSearch: (String) -> Unit,
    initialSearch: String = "avengers",
    viewModel: MainViewModel,
    onScrollChange: (Boolean) -> Unit
) {

    var searchText by rememberSaveable { mutableStateOf(initialSearch) }

    Column(modifier = Modifier.fillMaxSize()) {

        OutlinedTextField(
            value = searchText,
            onValueChange = { newValue ->
                searchText = newValue
                onSearch(newValue)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            singleLine = true,
            placeholder = {
                Text(
                    text = "Search movies...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = {
                if (searchText.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            searchText = ""
                            onSearch("")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            shape = RoundedCornerShape(12.dp),
            label = {
                Text(
                    text = "Search Movies",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        )

        val loadState = pagingItems.loadState

        when (loadState.refresh) {
            is LoadState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

            }

            is LoadState.Error -> {
                val error = (pagingItems.loadState.refresh as LoadState.Error).error
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(modifier = Modifier, text = "Error ${error.localizedMessage}")
                }
            }

            else -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    ScalableVerticalGrid(pagingItems, onNavigateToDetail, viewModel = viewModel,onScrollChange = onScrollChange)
                    if (loadState.append is LoadState.Loading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

            }
        }


        /*        LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.padding(8.dp),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(pagingItems.itemCount) { index ->
                        val movieItem = pagingItems[index]
                        MovieCard(
                            item = movieItem,
                            onClick = { onNavigateToDetail(movieItem?.Title ?: "") },
                            // Optionally, you can add a Modifier.padding here per card
                        )
                    }
                }*/

        /*        LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Adaptive(150.dp), // 2 columns, staggered effect
                    modifier = Modifier.padding(8.dp),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(pagingItems.itemCount) { index ->
                        val movieItem = pagingItems[index]
                        MovieCard(
                            item = movieItem,
                            onClick = { onNavigateToDetail(movieItem?.Title ?: "") },
                            // Optionally, you can add a Modifier.padding here per card
                        )
                    }
                }*/

        /*LazyColumn {
            items(pagingItems.itemCount) { index ->
                val movieItem = pagingItems[index]
                MovieCard(
                    item = movieItem,
                    onClick = { onNavigateToDetail(movieItem?.Title ?: "") }
                )
            }
        }*/
    }

}