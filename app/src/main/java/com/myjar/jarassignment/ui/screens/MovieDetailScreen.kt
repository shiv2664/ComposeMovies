package com.myjar.jarassignment.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.myjar.jarassignment.NetworkResult
import com.myjar.jarassignment.data.model.MovieDetails
import com.myjar.jarassignment.ui.vm.DetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    title: String?,
) {
    val viewModel: DetailsViewModel = hiltViewModel()
    val detailState by viewModel.details.collectAsState()

    LaunchedEffect(title) {
        if (!title.isNullOrEmpty()) {
            viewModel.getDetails(title)
        }
    }

/*     The scaffold provides the top bar and a consistent layout structure
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Details", color = Color.White) },
                navigationIcon = {
//                    IconButton(onClick = onBackPress) {
//                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
//                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent // Make TopAppBar transparent to see backdrop
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
         Main content area that respects the scaffold's padding*/
        Box(modifier = Modifier) {
            when (val state = detailState) {
                is NetworkResult.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is NetworkResult.Success -> {
                    state.data?.let {
                        movie -> MovieDetailsContent(movie = movie)
                    } ?: run {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Movie details not found.")
                        }
                    }
                }
                is NetworkResult.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error: ${state.message ?: "Unknown error"}")
                    }
                }
            }
        }
//    }
}

@Composable
fun MovieDetailsContent(movie: MovieDetails) {
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        // Backdrop Image with a gradient overlay
        Box(modifier = Modifier.fillMaxSize()) {
//            AsyncImage(
//                model = movie.Poster,
//                contentDescription = "Backdrop",
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .fillMaxHeight(0.5f)
//                    .blur(radius = 20.dp), // Take top half of the screen
//                contentScale = ContentScale.Crop
//            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
            ) {
//                Blurred backdrop
                AsyncImage(
                    model = movie.Poster,
                    contentDescription = "Backdrop",
                    modifier = Modifier
                        .fillMaxSize()
                        .fillMaxHeight(0.5f)
                        .blur(5.dp), // apply blur
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f)) // darken for contrast
                )

                // Foreground content (sharp)
/*                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                ) {
                    Text(
                        text = movie.Title ?: "Unknown Title",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = movie.Year ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }*/
            }



            // Gradient to make content on top more readable
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.background
                            ),
                            startY = 600f // Adjust gradient start
                        )
                    )
            )
        }

        // Scrollable column for all the movie details
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 32.dp) // Padding at the bottom
        ) {
            // Spacer to push content below the semi-transparent part of the backdrop
            Spacer(modifier = Modifier.height(180.dp))

            // Main Poster and Header information
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                AsyncImage(
                    model = movie.Poster,
                    contentDescription = "Movie Poster",
                    modifier = Modifier
                        .width(140.dp)
                        .height(210.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = movie.Title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${movie.Year} • ${movie.Genre} • ${movie.Runtime}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // IMDB Rating section
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "IMDB Rating",
                    tint = Color(0xFFFFC107), // Yellow star color
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = movie.imdbRating,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "/10 IMDb",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 4.dp, top = 6.dp)
                )
            }

            // Plot Summary section
            DetailSection(title = "Plot Summary") {
                Text(
                    text = movie.Plot,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 24.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Cast & Crew section
            DetailSection(title = "Cast & Crew") {
                InfoRow("Director", movie.Director)
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow("Writer", movie.Writer)
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow("Actors", movie.Actors)
            }
        }
    }
}

@Preview
@Composable
fun MovieDetailsContentPreview() {
    val movie = MovieDetails(
        Actors = "Actor 1, Actor 2",
        Awards = "Best Movie Award",
        BoxOffice = "N/A",
        Country = "USA",
        DVD = "N/A",
        Director = "Director Name",
        Genre = "Action",
        Language = "English",
        Metascore = "N/A",
        Plot = "This is a plot summary of the movie.",
        Poster = "https://example.com/poster.jpg",
        Production = "N/A",
        Rated = "PG-13",
        Ratings = emptyList(),
        Released = "01 Jan 2023",
        Response = "True",
        Runtime = "120 min",
        Title = "Movie Title",
        Type = "movie",
        Website = "N/A",
        Writer = "Writer Name",
        Year = "2023",
        imdbID = "tt1234567",
        imdbRating = "8.0",
        imdbVotes = "100,000"
    )
    MovieDetailsContent(movie = movie)
}

@Composable
private fun DetailSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        content()
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
    }
}