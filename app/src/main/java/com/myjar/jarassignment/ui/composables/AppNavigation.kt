package com.myjar.jarassignment.ui.composables

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.runtime.*
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.LazyPagingItems
import coil.compose.AsyncImage
import com.myjar.jarassignment.NetworkResult
import com.myjar.jarassignment.Utils.slideLeftEnter
import com.myjar.jarassignment.Utils.slideLeftExit
import com.myjar.jarassignment.Utils.slideRightEnter
import com.myjar.jarassignment.Utils.slideRightExit
import com.myjar.jarassignment.data.model.Search
import com.myjar.jarassignment.ui.vm.DetailsViewModel
import com.myjar.jarassignment.ui.vm.MainViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    var searchQuery by rememberSaveable { mutableStateOf("avengers") }
    val viewModel = hiltViewModel<MainViewModel>()
    val pagingItems = viewModel.getMoviesListing(searchKey = searchQuery).collectAsLazyPagingItems()

    NavHost(modifier = modifier, navController = navController, startDestination = "item_list") {
        composable(
            "item_list",
            /*enterTransition = slideLeftEnter,
            exitTransition = slideLeftExit,
            popEnterTransition = slideRightEnter,
            popExitTransition = slideRightExit*/


            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(700)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(700)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(700)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(700)
                )
            }
        ) {

            ItemListScreen(
                pagingItems,
                onNavigateToDetail = { title -> navController.navigate("item_detail/$title") },
                onSearch = { newQuery -> searchQuery = newQuery },
                initialSearch = searchQuery
            )
        }
        composable("item_detail/{title}",
/*            enterTransition = slideLeftEnter,
            exitTransition = slideLeftExit,
            popEnterTransition = slideRightEnter,
            popExitTransition = slideRightExit*/


            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(700)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(700)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(700)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(700)
                )
            }
        ) { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title")
            ItemDetailScreen(title = title)
        }
    }
}

@Composable
fun ItemListScreen(
    pagingItems: LazyPagingItems<Search>,
    onNavigateToDetail: (String) -> Unit,
    onSearch: (String) -> Unit,
    initialSearch: String = "avengers"
) {

    var searchText by rememberSaveable { mutableStateOf(initialSearch) }

    Column(modifier = Modifier.fillMaxSize()) {

        TextField(value = searchText,
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

        LazyColumn {
            items(pagingItems.itemCount) { index ->
                val movieItem = pagingItems[index]
                MovieCard(
                    item = movieItem,
                    onClick = { onNavigateToDetail(movieItem?.Title ?: "") }
                )
            }
        }
    }

}

@Composable
fun MovieCard(
    item: Search?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val posterUrl = item?.Poster
//    var dominantColor by remember { mutableStateOf(Color.White) }

    var gradientColors by remember { mutableStateOf(listOf(Color.White, Color.LightGray)) }
    // Extract dominant color from remote image with Palette, using Compose only
    /*LaunchedEffect(posterUrl) {
        posterUrl?.let { url ->
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(url)
                .allowHardware(false)
                .build()
            val result = loader.execute(request)
            if (result is SuccessResult) {
                val bitmap = (result.drawable as? BitmapDrawable)?.bitmap
                bitmap?.let {
                    withContext(Dispatchers.Default) {
                        Palette.Builder(it).generate { palette ->
                            palette?.let { pal ->
                                // Extract dominant color
                                var dominantColor = pal.getDominantColor(android.graphics.Color.WHITE)

                                // Convert to HSL and adjust brightness
                                val hsl = FloatArray(3)
                                ColorUtils.colorToHSL(dominantColor, hsl)
                                if (hsl[2] > 0.7f) {
                                    hsl[2] = 0.4f
                                    dominantColor = ColorUtils.HSLToColor(hsl)
                                }
                                // Lighter right-side color
                                val lighterHsl = hsl.copyOf()
                                lighterHsl[2] = (lighterHsl[2] + 0.25f).coerceAtMost(1f)
                                val lighterColor = ColorUtils.HSLToColor(lighterHsl)

                                // Update Compose state with gradient colors
                                gradientColors = listOf(Color(dominantColor), Color(lighterColor))
                            }
                        }
                    }
                }
            }
        }
    }*/


    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            AsyncImage(
                model = posterUrl,
                contentDescription = "Product image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.FillBounds
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item?.Title ?: "",
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(text = item?.imdbID ?: "", color = Color.Black)
            Text(text = item?.Year ?: "", color = Color.Black)
        }
    }
}


/*@Composable
fun MovieCard(
    item: Search?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            AsyncImage(
                model = item?.Poster,
                contentDescription = "Product image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Fit)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item?.Title?:"",
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
                Text(text = item?.imdbID?:"", color =  Color.Black)
                Text(text = item?.Year?:"", color = Color.Black)

        }
    }
}*/

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
