package com.myjar.jarassignment.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.myjar.jarassignment.data.model.Search

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
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
        ) {
            AsyncImage(
                model = posterUrl,
                contentDescription = "Product image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .clickable { onClick() },
                contentScale = ContentScale.FillBounds
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(modifier = Modifier.padding(start = 8.dp), text = item?.Title ?: "", fontWeight = FontWeight.Bold, color = Color.Black)
            Text(modifier = Modifier.padding(start = 8.dp), text = item?.imdbID ?: "", color = Color.Black)
            Text(modifier = Modifier.padding(start = 8.dp, bottom = 8.dp), text = item?.Year ?: "", color = Color.Black)
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

@Preview
@Composable
fun MovieCardPreview() {
    val item = Search(
        Poster = "https://m.media-amazon.com/images/M/MV5BNDYxNjQyMjAtNTdiOS00NGYwLWFmNTAtNThmYjU5ZGI2YTI1XkEyXkFqcGdeQXVyMTMxODk2OTU@._V1_SX300.jpg",
        Title = "The Avengers",
        Type = "movie",
        Year = "2012",
        imdbID = "tt0848228"
    )
    MovieCard(
        item = item,
        onClick = {},
    )
}

