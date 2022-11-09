package io.joelt.texttemplate.ui.screens.template_edit

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.*
import io.joelt.texttemplate.navigation.*

fun NavHostController.navigateToTemplateEdit(templateId: Long) {
    this.navigate("templates/$templateId/edit")
}

class TemplateEditScreen : Screen {
    override val route: String = "templates/{templateId}/edit"
    override val arguments: List<NamedNavArgument> = listOf(
        navArgument("templateId") { type = NavType.LongType }
    )

    override fun scaffold(nav: NavHostController) = ScaffoldOptions(
        topBar = { TemplateEditTopNavBar(nav) }
    )

    @Composable
    override fun Composable(backStackEntry: NavBackStackEntry, nav: NavHostController) {
            val templateId = backStackEntry.arguments!!.getLong("templateId")
            TemplateEditScreen(nav, templateId)
        }
}

@Composable
private fun TemplateEditScreen(nav: NavHostController, templateId: Long) {
    Text(text = "template edit screen for template with id $templateId")
}
