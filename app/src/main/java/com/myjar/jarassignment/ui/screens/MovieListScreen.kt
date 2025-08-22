package com.myjar.jarassignment.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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

@Composable
fun ItemListScreen(
    pagingItems: LazyPagingItems<Search>,
    onNavigateToDetail: (String) -> Unit,
    onSearch: (String) -> Unit,
    initialSearch: String = "avengers"
) {

    var searchText by rememberSaveable { mutableStateOf(initialSearch) }

    Column(modifier = Modifier.fillMaxSize()) {

        TextField(
            value = searchText,
            onValueChange = { newValue ->
                searchText = newValue
                onSearch(newValue)
            }, modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            singleLine = true,
            placeholder = {
                Text("Search...")
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
                    ScalableVerticalGrid(pagingItems, onNavigateToDetail)
                    if (loadState.append is LoadState.Loading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
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