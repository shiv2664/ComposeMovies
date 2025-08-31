package com.myjar.jarassignment.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.myjar.jarassignment.R // Assuming you have a placeholder drawable
import com.myjar.jarassignment.data.model.Search
import com.myjar.jarassignment.ui.vm.MainViewModel

@Composable
fun FavouriteScreen(
    viewModel: MainViewModel,
    onMovieClick: (String) -> Unit // Callback for when a movie item is clicked
) {
    val favoriteMovies by viewModel.favoriteMovies.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        if (favoriteMovies.isEmpty()) {
            Text(
                text = "No favourite movies yet.",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(favoriteMovies, key = { it.imdbID }) {
                    movie -> FavoriteMovieItem(movie = movie, viewModel = viewModel, onMovieClick = onMovieClick)
                }
            }
        }
    }
}

@Composable
fun FavoriteMovieItem(
    movie: Search,
    viewModel: MainViewModel,
    onMovieClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onMovieClick(movie.imdbID) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(movie.Poster)
                    .crossfade(true)
//                    .placeholder(R.drawable.baseline_connected_tv_24) // Replace with your placeholder
//                    .error(R.drawable.baseline_connected_tv_24) // Replace with your error placeholder
                    .build(),
                contentDescription = movie.Title,
                modifier = Modifier
                    .size(100.dp, 150.dp)
                    .padding(end = 8.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(text = movie.Title, style = MaterialTheme.typography.titleMedium)
                Text(text = movie.Year, style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = { viewModel.removeFavoriteMovie(movie.imdbID) }) {
                Icon(Icons.Filled.Delete, contentDescription = "Remove from favorites")
            }
        }
    }
}
