package io.joelt.texttemplate.ui.screens

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import io.joelt.texttemplate.navigation.*

class DraftsScreen : Screen {
    override fun route(): String = "drafts"

    override fun arguments(): List<NamedNavArgument> = listOf()

    override fun makeComposable(backStackEntry: NavBackStackEntry): ScreenComposable =
        { nav, scaffold ->
            DraftsScreen(nav, scaffold)
        }
}

@Composable
private fun DraftsScreen(nav: NavHostController, scaffold: ScaffoldController) {
    scaffold.changeNavBars(ScaffoldType.HOME_SCREEN)
    Text(text = "drafts screen")
}
