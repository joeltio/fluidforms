package io.joelt.texttemplate.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun NavHostController.currentRouteAsState(): String? {
    return this.currentBackStackEntryAsState().value?.destination?.route
}

@Composable
fun NavHostController.atRoute(dest: String): Boolean {
    return currentRouteAsState() == dest
}

fun NavHostController.navigateClearStack(dest: String) {
    this.navigate(dest) {
        popUpTo(graph.findStartDestination().id)
    }
}
