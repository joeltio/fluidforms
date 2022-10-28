package io.joelt.texttemplate.ui.screens

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import io.joelt.texttemplate.navigation.*

class SettingsScreen : Screen {
    override fun route(): String = "settings"

    override fun arguments(): List<NamedNavArgument> = listOf()

    override fun makeComposable(backStackEntry: NavBackStackEntry): ScreenComposable =
        { nav, scaffold ->
            SettingsScreen(nav, scaffold)
        }
}

@Composable
private fun SettingsScreen(nav: NavHostController, scaffold: ScaffoldController) {
    scaffold.changeNavBars(ScaffoldType.SETTINGS_SCREEN)
    Text(text = "settings screen")
}
