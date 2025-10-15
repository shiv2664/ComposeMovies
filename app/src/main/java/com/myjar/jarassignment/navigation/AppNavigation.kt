package com.myjar.jarassignment.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.myjar.jarassignment.ui.screens.FavouriteScreen
import com.myjar.jarassignment.ui.screens.MovieDetailScreen
import com.myjar.jarassignment.ui.screens.MovieListScreen
import com.myjar.jarassignment.ui.screens.ViewPagerScreen
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

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { screen.icon() },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
//                        It checks whether the current screen in the navigation stack matches the route of this bottom bar item and marks it as selected if true.
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Listing.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(
                Screen.Listing.route,
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
                MovieListScreen(
                    pagingItems,
                    onNavigateToDetail = { title, movie ->
                        navController.navigate("item_detail/$title")
                    },
                    onSearch = { newQuery -> searchQuery = newQuery },
                    initialSearch = searchQuery,
                    viewModel
                )
            }


            composable(
                "item_detail/{title}",
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
                MovieDetailScreen(title = title)
            }
            composable(Screen.ViewPagerScreen.route) {
                ViewPagerScreen()
            }
            composable(Screen.Favorites.route) {
                FavouriteScreen(
                    viewModel = viewModel,
                    onMovieClick = {}
                )
            }
        }
    }
}
