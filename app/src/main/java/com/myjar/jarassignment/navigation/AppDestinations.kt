package com.myjar.jarassignment.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable

// Placeholder for your actual navigation destinations
sealed class Screen(val route: String, val label: String, val icon: @Composable () -> Unit) {
    object Listing : Screen("item_list", "Listing", { Icon(Icons.Filled.Home, contentDescription = "Home") })
    object ViewPagerScreen : Screen("search", "ViewPager", { Icon(Icons.Filled.Warning, contentDescription = "Search") }) // Added a placeholder Search screen
    object Favorites : Screen("favorites", "Favorites", { Icon(Icons.Filled.Favorite, contentDescription = "Favorites") }) // Added a placeholder Favorites screen
}

val items = listOf(
    Screen.Listing,
    Screen.ViewPagerScreen,
    Screen.Favorites,
)

data class DataScreens(val route:String, val label: String, val icon:@Composable () -> Unit)

val itemsDataList: List<DataScreens> = listOf(DataScreens(route ="",label="",icon={ Icon(Icons.Filled.Home, contentDescription = "Home") }),
    DataScreens("search", "ViewPager", { Icon(Icons.Filled.Warning, contentDescription = "Search") }),
    DataScreens("favorites", "Favorites", { Icon(Icons.Filled.Favorite, contentDescription = "Favorites") }))