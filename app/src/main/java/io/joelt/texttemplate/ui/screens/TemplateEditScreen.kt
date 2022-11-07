package io.joelt.texttemplate.ui.screens

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.*
import io.joelt.texttemplate.navigation.*

fun NavHostController.navigateToTemplateEdit(templateId: Long) {
    this.navigate("templates/$templateId/edit")
}

class TemplateEditScreen : Screen {
    override fun route(): String = "templates/{templateId}/edit"

    override fun arguments(): List<NamedNavArgument> = listOf(
        navArgument("templateId") { type = NavType.LongType }
    )

    override fun makeComposable(backStackEntry: NavBackStackEntry): ScreenComposable =
        { nav, scaffold ->
            scaffold.changeNavBars(ScaffoldType.TEMPLATE_EDIT_SCREEN)
            val templateId = backStackEntry.arguments!!.getLong("templateId")
            TemplateEditScreen(nav, scaffold, templateId)
        }
}

@Composable
private fun TemplateEditScreen(nav: NavHostController, scaffold: ScaffoldController, templateId: Long) {
    Text(text = "template edit screen for template with id $templateId")
}
