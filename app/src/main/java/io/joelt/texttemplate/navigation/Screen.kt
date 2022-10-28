package io.joelt.texttemplate.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController

typealias ScreenComposable = @Composable (NavHostController, ScaffoldController) -> Unit

interface Screen {
    fun route(): String
    fun arguments(): List<NamedNavArgument>
    fun makeComposable(backStackEntry: NavBackStackEntry): ScreenComposable
}
