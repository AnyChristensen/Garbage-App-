package dk.christensen.garbagefinalassignment.ui.Features.garbage

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import androidx.navigation.navOptions

fun NavGraphBuilder.garbageNavGraph(navController: NavHostController) {

    val singleTopNavOptions: NavOptions = navOptions {
        launchSingleTop = true
    }

// The nested Garbage Graph (containing: SortingScreen -> GarbageList -> Details (edit item) -> AddItemGraph)
    navigation<GarbageGraph>(startDestination = GarbageSorting) {

        // Sorting screen
        composable<GarbageSorting> {
            GarbageSortingScreen(
                onNavigate = { event ->
                    when (event) {

                        is GarbageSortingViewModel.NavigationEvent.NavigateToGarbageList ->
                            navController.navigate(GarbageList())

                        is GarbageSortingViewModel.NavigationEvent.NavigateToAffaldKBH ->
                            navController.navigate(AffaldKbh)

                        is GarbageSortingViewModel.NavigationEvent.NavigateToCameraScreen ->
                            navController.navigate(CameraScreen)
                    }
                }
            )
        }

        // List Screen
        composable<GarbageList>(deepLinks = listOf (navDeepLink {
                uriPattern = "garbage://items/{itemId}"
                }
             )
        ) {
            GarbageListScreen(
                onNavigate = { event ->
                    when (event) {

                        is GarbageListViewModel.NavigationEvent.NavigateUp ->
                            navController.popBackStack()

                        is GarbageListViewModel.NavigationEvent.NavigateToAddWhat ->
                            navController.navigate(AddWhat)

                    }
                }
            )
        }


        // Adding the nested graph AddItemGraph
        navigation<AddItemGraph>(startDestination = AddWhat)
        {
            // Navigation for the AddWhat screen
            composable<AddWhat>(
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(durationMillis = 500)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(durationMillis = 500)
                    )
                },
                popEnterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(durationMillis = 500)
                    )
                },
                popExitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(durationMillis = 500)
                    )
                }
            ) {
                AddWhatScreen(
                    onNavigate = { event ->
                        when (event) {
                            is AddWhatViewModel.NavigationEvent.NavigateUp -> navController.popBackStack()
                            is AddWhatViewModel.NavigationEvent.NavigateToAddWhere -> navController.navigate(
                                route = AddWhere(what = event.what),
                                navOptions = singleTopNavOptions
                            )
                        }
                    }
                )
            }
            dialog<AddWhere> {
                AddWhereScreen(
                    onNavigate = { event ->
                        when (event) {
                            is AddWhereViewModel.NavigationEvent.CloseDialog -> navController.popBackStack()
                            is AddWhereViewModel.NavigationEvent.NavigateToGarbageList -> navController.navigate(
                                route = GarbageList(),
                                navOptions = navOptions {
                                    popUpTo(route = GarbageGraph)
                                }
                            )
                        }
                    }
                )
            }

            // Affald KBH Screen
            composable<AffaldKbh> {

                AffaldKbhScreen(
                    onUpClick = {navController.popBackStack()}
                )
            }

            composable<AffaldKbh> {
                AffaldKbhScreen(
                    onUpClick = {
                        navController.popBackStack()
                    }
                )
            }
            // Scan Garbage Screen
            composable<CameraScreen> {

                CameraScreen(
                    onNavigate = { event ->

                        when(event) {
                            is CameraViewModel.NavigationEvent.NavigateUp ->
                                navController.popBackStack()
                        }
                    }
                )
            }

        }
    }
}