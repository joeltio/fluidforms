package io.joelt.texttemplate.ui.screens.settings

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import io.joelt.texttemplate.navigation.*

class SettingsScreen : Screen {
    override fun route(): String = "settings"

    override fun arguments(): List<NamedNavArgument> = listOf()

    @Composable
    override fun Composable(backStackEntry: NavBackStackEntry, nav: NavHostController) {
        SettingsScreen(nav)
    }
}

@Composable
private fun SettingsScreen(nav: NavHostController) {
    Text(text = "settings screen")
}
