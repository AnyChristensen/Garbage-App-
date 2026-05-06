package dk.christensen.garbagefinalassignment.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dk.christensen.garbagefinalassignment.ui.Features.garbage.AffaldKbh
import dk.christensen.garbagefinalassignment.ui.Features.garbage.AffaldKbhScreen
import dk.christensen.garbagefinalassignment.ui.Features.garbage.CameraScreen
import dk.christensen.garbagefinalassignment.ui.Features.garbage.garbageNavGraph
import dk.christensen.garbagefinalassignment.ui.Features.recycling.Bins
import dk.christensen.garbagefinalassignment.ui.Features.recycling.RecyclingScreen
import dk.christensen.garbagefinalassignment.ui.Features.settings.Settings
import dk.christensen.garbagefinalassignment.ui.Features.settings.SettingsScreen


@Composable
fun MainNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val destinations = listOf(
        BottomNavigation.GarbageTab,
        BottomNavigation.BinsTab,
        BottomNavigation.SettingsTab
    )
    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                destinations.forEach { screen ->

                    NavigationBarItem(
                        icon = { Icon(imageVector = screen.icon, contentDescription = null) },
                        label = { Text(text = stringResource(id = screen.title)) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route::class.qualifiedName } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true // Avoiding duplicates if repeated clicks
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    )
    { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavigation.GarbageTab.route, // GarbageGraph
            modifier = Modifier.padding(innerPadding)
        ) {
            // The nested garbage graph
            garbageNavGraph(navController = navController)
            composable<Bins> {
                RecyclingScreen()
            }
            composable<Settings> {
                SettingsScreen()
            }
            composable<AffaldKbh> {
                AffaldKbhScreen()
            }

            composable<CameraScreen> {
                CameraScreen()
            }
        }

    }
}

