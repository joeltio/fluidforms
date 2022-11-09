package io.joelt.texttemplate.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController

interface Screen {
    fun route(): String
    fun arguments(): List<NamedNavArgument>
    @Composable
    fun Composable(backStackEntry: NavBackStackEntry, nav: NavHostController)
}
