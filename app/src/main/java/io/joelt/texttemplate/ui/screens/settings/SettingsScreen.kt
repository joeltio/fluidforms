package io.joelt.texttemplate.ui.screens.settings

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import io.joelt.texttemplate.navigation.*

val Route.settings: String
    get() = "settings"

class SettingsScreen : Screen {
    override val route: String = Route.settings
    override val arguments: List<NamedNavArgument> = listOf()

    @Composable
    override fun scaffold(nav: NavHostController) = ScaffoldOptions(
        topBar = { SettingsTopNavBar(nav) }
    )

    @Composable
    override fun Composable(backStackEntry: NavBackStackEntry, nav: NavHostController) {
        SettingsScreen(nav)
    }
}

@Composable
private fun SettingsScreen(nav: NavHostController) {
    Text(text = "settings screen")
}
