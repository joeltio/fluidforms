package io.joelt.texttemplate.ui.screens

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.*
import io.joelt.texttemplate.navigation.*

class TemplateEditScreen : Screen {
    override fun route(): String = "templates/{templateId}/edit"

    override fun arguments(): List<NamedNavArgument> = listOf(
        navArgument("templateId") { type = NavType.IntType }
    )

    override fun makeComposable(backStackEntry: NavBackStackEntry): ScreenComposable =
        { nav, scaffold ->
            val templateId = backStackEntry.arguments!!.getInt("templateId")
            TemplateEditScreen(nav, scaffold, templateId)
        }
}

@Composable
private fun TemplateEditScreen(nav: NavHostController, scaffold: ScaffoldController, templateId: Int) {
    scaffold.changeNavBars(ScaffoldType.TEMPLATE_EDIT_SCREEN)
    Text(text = "template edit screen for template with id $templateId")
}
