package io.joelt.texttemplate.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController

interface Screen {
    val route: String
    val arguments: List<NamedNavArgument>
    @Composable
    fun scaffold(nav: NavHostController): ScaffoldOptions

    @Composable
    fun Composable(backStackEntry: NavBackStackEntry, nav: NavHostController)
}
