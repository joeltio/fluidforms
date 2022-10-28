package io.joelt.texttemplate.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun NavHostController.atRoute(dest: String): Boolean {
    val current = this.currentBackStackEntryAsState().value?.destination?.route
    return current == dest
}

fun NavHostController.navigateBottomNav(dest: String) {
    this.navigate(dest) {
        this@navigateBottomNav.graph.startDestinationRoute?.let { route ->
            popUpTo(route) {
                saveState = true
            }
        }
        launchSingleTop = true
        restoreState = true
    }
}
