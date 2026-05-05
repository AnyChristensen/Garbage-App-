package dk.christensen.garbagefinalassignment.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.ui.graphics.vector.ImageVector
import dk.christensen.garbagefinalassignment.R
import dk.christensen.garbagefinalassignment.ui.Features.settings.Settings
import dk.christensen.garbagefinalassignment.ui.Features.garbage.GarbageGraph
import dk.christensen.garbagefinalassignment.ui.Features.recycling.Bins

interface AppRoute

interface NestedGraph : AppRoute {
    val startDestination: AppRoute
}

sealed class BottomNavigation(
    val route: AppRoute,
    val title: Int,
    val icon: ImageVector
) {
    data object GarbageTab : BottomNavigation(
        route = GarbageGraph,
        title = R.string.garbage_screen_title,
        icon = Icons.Default.Delete
    )

    data object BinsTab : BottomNavigation(
        route = Bins,
        title = R.string.bins_screen_title,
        icon = Icons.Default.Recycling
    )

    data object SettingsTab : BottomNavigation(
        route = Settings,
        title = R.string.settings_screen_title,
        icon = Icons.Default.Settings
    )
}