package io.joelt.texttemplate.ui.screens.template_edit

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.*
import androidx.navigation.compose.rememberNavController
import io.joelt.texttemplate.AppScaffold
import io.joelt.texttemplate.models.Template
import io.joelt.texttemplate.models.genTemplates
import io.joelt.texttemplate.navigation.*
import io.joelt.texttemplate.ui.components.EditorLayout
import io.joelt.texttemplate.ui.components.SlotsEditor
import io.joelt.texttemplate.ui.theme.TextTemplateTheme
import org.koin.androidx.compose.koinViewModel

fun NavHostController.navigateToTemplateEdit(templateId: Long) {
    this.navigate("templates/$templateId/edit")
}

fun NavHostController.navigateToCreateTemplate() {
    this.navigate("templates/0/edit")
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
private fun TemplateEditScreenContent(
    template: Template?,
    onTemplateUpdate: (Template) -> Unit
) {
    if (template == null) {
        Spacer(Modifier.height(32.dp))
        CircularProgressIndicator()
        return
    }

    EditorLayout(
        name = template.name,
        onNameChange = { onTemplateUpdate(template.copy(name = it)) }
    ) {
        SlotsEditor(
            slots = template.slots,
            onSlotsChange = { onTemplateUpdate(template.copy(slots = it)) })
    }
}

@Composable
private fun TemplateEditScreen(
    nav: NavHostController,
    templateId: Long,
    viewModel: TemplateEditViewModel = koinViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadTemplate(templateId)
    }

    TemplateEditScreenContent(viewModel.template) {
        viewModel.template = it
    }
}

@Preview(showBackground = true)
@Composable
private fun TemplateEditScreenPreview() {
    var template by remember {
        mutableStateOf(genTemplates(1)[0])
    }

    val screen = TemplateEditScreen()
    val nav = rememberNavController()
    TextTemplateTheme {
        AppScaffold(scaffoldOptions = screen.scaffold(nav)) {
            TemplateEditScreenContent(template) { template = it }
        }
    }
}
