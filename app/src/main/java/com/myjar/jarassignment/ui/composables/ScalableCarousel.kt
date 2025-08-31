package com.myjar.jarassignment.ui.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import androidx.paging.compose.LazyPagingItems
import com.myjar.jarassignment.data.model.Search
import com.myjar.jarassignment.ui.vm.MainViewModel
import kotlin.let


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScalableVerticalGrid(
    pagingItems: LazyPagingItems<Search>,
    onNavigateToDetail: (String, Search) -> Unit,
    viewModel: MainViewModel
) {
    val listState = rememberLazyGridState()

    LazyVerticalGrid(
        state = listState,
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(pagingItems.itemCount) { index ->
            val item = pagingItems[index] ?: return@items

//            Use offset.y + size.height when working with LazyVerticalGrid.
//            Use offset.x + size.width when working with LazyHorizontalGrid.

            val layoutInfo = listState.layoutInfo
            val itemInfo = layoutInfo.visibleItemsInfo.find { it.index == index }

            val viewportStart = layoutInfo.viewportStartOffset
            val viewportEnd = layoutInfo.viewportEndOffset
            val viewportCenter = (viewportStart + viewportEnd) / 2f

            val itemCenter = itemInfo?.let { it.offset.y + it.size.height / 2f } ?: 0f
            val distance = abs(viewportCenter - itemCenter)

            // Scale: 1f → 1.1f
            val scale = 1f + (0.1f * (1f - (distance / viewportCenter).coerceIn(0f, 1f)))

            // Alpha: 0.6f → 1f
            val alpha = 0.6f + (0.4f * (1f - (distance / viewportCenter).coerceIn(0f, 1f)))

            Box(
                modifier = Modifier
                    .padding(8.dp) // reserve space
                    .aspectRatio(0.7f) // card shape like movie poster
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
            ) {
                MovieCard(item,onClick = { onNavigateToDetail(item.Title,item) }, modifier = Modifier)
            }
        }
    }
}


/*@Composable
fun ScalableCarousel(
    items: List<String>
) {
    val listState = rememberLazyListState()
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current // <-- Get the current density

    LazyRow(
        state = listState,
        contentPadding = PaddingValues(horizontal = 64.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        itemsIndexed(items) { index, item ->
            // Get screen width in Dp
            val screenWidthDp = configuration.screenWidthDp.dp

            // Convert screen width in Dp to Px using LocalDensity
            val screenWidthPx = with(density) { screenWidthDp.toPx() } // <-- Correct conversion
            val center = screenWidthPx / 2f

            val layoutInfo = listState.layoutInfo
            val itemInfo = layoutInfo.visibleItemsInfo.find { it.index == index }
            val itemCenter = itemInfo?.let {
                (it.offset + it.size / 2).toFloat()
            } ?: 0f
            val distance = abs(center - itemCenter)

            // Scale: 1f → 1.2f
            val scale = 1f + (0.2f * (1f - (distance / center).coerceIn(0f, 1f)))

            // Alpha: 0.5f → 1f
            val alpha = 0.5f + (0.5f * (1f - (distance / center).coerceIn(0f, 1f)))

            Card(
                modifier = Modifier
                    .size(160.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha
                    },
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = item,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}*/
