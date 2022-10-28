package io.joelt.texttemplate.ui.screens

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import io.joelt.texttemplate.navigation.*

class ArchivedScreen : Screen {
    override fun route(): String = "archived"

    override fun arguments(): List<NamedNavArgument> = listOf()

    override fun makeComposable(backStackEntry: NavBackStackEntry): ScreenComposable =
        { nav, scaffold ->
            ArchivedScreen(nav, scaffold)
        }
}

@Composable
private fun ArchivedScreen(nav: NavHostController, scaffold: ScaffoldController) {
    scaffold.changeNavBars(ScaffoldType.HOME_SCREEN)
    Text(text = "archived screen")
}
