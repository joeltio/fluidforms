package io.joelt.texttemplate.ui.screens

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.*
import io.joelt.texttemplate.navigation.*

class TemplateCreateScreen : Screen {
    override fun route(): String = "templates/create"

    override fun arguments(): List<NamedNavArgument> = listOf()

    override fun makeComposable(backStackEntry: NavBackStackEntry): ScreenComposable =
        { nav, scaffold ->
            scaffold.changeNavBars(ScaffoldType.TEMPLATE_CREATE_SCREEN)
            TemplateCreateScreen(nav, scaffold)
        }
}

@Composable
private fun TemplateCreateScreen(nav: NavHostController, scaffold: ScaffoldController) {
    Text(text = "template create screen")
}
