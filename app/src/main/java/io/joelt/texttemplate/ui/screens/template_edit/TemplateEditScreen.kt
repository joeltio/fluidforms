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
import io.joelt.texttemplate.ui.components.SlotsEditorState
import io.joelt.texttemplate.ui.theme.TextTemplateTheme
import org.koin.androidx.compose.koinViewModel

fun NavHostController.navigateToTemplateEdit(templateId: Long) {
    this.navigate("templates/$templateId/edit")
}

fun NavHostController.navigateToCreateTemplate() {
    this.navigate("templates/0/edit")
}

class TemplateEditController {
    var onSave = {}
}

class TemplateEditScreen : Screen {
    val controller = TemplateEditController()

    override val route: String = "templates/{templateId}/edit"
    override val arguments: List<NamedNavArgument> = listOf(
        navArgument("templateId") { type = NavType.LongType }
    )

    override fun scaffold(nav: NavHostController) = ScaffoldOptions(
        topBar = { TemplateEditTopNavBar(nav, onSave = { controller.onSave() }) }
    )

    @Composable
    override fun Composable(backStackEntry: NavBackStackEntry, nav: NavHostController) {
        val templateId = backStackEntry.arguments!!.getLong("templateId")
        TemplateEditScreen(nav, controller, templateId)
    }
}

data class TemplateEditState(
    val template: Template?,
    val editorState: SlotsEditorState? = template?.let {
        SlotsEditorState(template.slots)
    }
) {
    fun withTemplateName(name: String): TemplateEditState =
        copy(template = template?.copy(name = name))

    fun withEditorState(editorState: SlotsEditorState) =
        copy(
            template = template?.copy(slots = editorState.slots),
            editorState = editorState
        )
}

@Composable
private fun TemplateEditScreenContent(
    state: TemplateEditState,
    onStateChange: (TemplateEditState) -> Unit,
) {
    if (state.template == null) {
        Spacer(Modifier.height(32.dp))
        CircularProgressIndicator()
        return
    }
    state.editorState!!

    EditorLayout(
        name = state.template.name,
        onNameChange = { onStateChange(state.withTemplateName(it)) }
    ) {
        SlotsEditor(state.editorState) {
            onStateChange(state.withEditorState(it))
        }
    }
}

@Composable
private fun TemplateEditScreen(
    nav: NavHostController,
    screenController: TemplateEditController,
    templateId: Long,
    viewModel: TemplateEditViewModel = koinViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadTemplate(templateId)
        screenController.onSave = {
            viewModel.saveTemplate {
                nav.navigate("templates")
            }
        }
    }

    TemplateEditScreenContent(viewModel.screenState) {
        viewModel.screenState = it
    }
}

@Preview(showBackground = true)
@Composable
private fun TemplateEditScreenPreview() {
    val template = genTemplates(1)[0]
    var screenState by remember { mutableStateOf(TemplateEditState(template)) }

    val screen = TemplateEditScreen()
    val nav = rememberNavController()
    TextTemplateTheme {
        AppScaffold(scaffoldOptions = screen.scaffold(nav)) {
            TemplateEditScreenContent(screenState) { screenState = it }
        }
    }
}
