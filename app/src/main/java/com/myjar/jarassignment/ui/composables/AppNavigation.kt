package com.myjar.jarassignment.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.LazyPagingItems
import coil.compose.AsyncImage
import com.myjar.jarassignment.data.model.Search
import com.myjar.jarassignment.ui.vm.JarViewModel

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier, ) {
    val navController = rememberNavController()

    val viewModel = hiltViewModel<JarViewModel>()
    val pagingItems= viewModel.getMoviesListing().collectAsLazyPagingItems()
    NavHost(modifier = modifier, navController = navController, startDestination = "item_list") {
        composable("item_list") {
            ItemListScreen(
                pagingItems,
                onNavigateToDetail = { itemId -> navController.navigate("item_detail/$itemId")})
        }
        composable("item_detail/{itemId}") { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId")
            ItemDetailScreen(itemId = itemId)
        }
    }
}

@Composable
fun ItemListScreen(
    pagingItems: LazyPagingItems<Search>,
    onNavigateToDetail: (String) -> Unit
) {
    LazyColumn{
       items(pagingItems.itemCount) { index ->
           val movieItem = pagingItems[index]
               MovieCard(
                   item = movieItem,
                   onClick = { onNavigateToDetail(movieItem?.Title?:"")}
               )
       }
   }

}

@Composable
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
}

@Composable
fun ItemDetailScreen(itemId: String?) {
    // Fetch the item details based on the itemId
    // Here, you can fetch it from the ViewModel or repository
    Text(
        text = "Item Details for ID: $itemId",
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    )
}
