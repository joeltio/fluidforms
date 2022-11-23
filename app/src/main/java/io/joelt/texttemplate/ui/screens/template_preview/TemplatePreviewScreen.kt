package io.joelt.texttemplate.ui.screens.template_preview

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.*
import androidx.navigation.compose.rememberNavController
import io.joelt.texttemplate.AppScaffold
import io.joelt.texttemplate.R
import io.joelt.texttemplate.models.Template
import io.joelt.texttemplate.models.genTemplates
import io.joelt.texttemplate.navigation.*
import io.joelt.texttemplate.ui.components.TemplatePreview
import io.joelt.texttemplate.ui.screens.draft_edit.createDraft
import io.joelt.texttemplate.ui.screens.template_edit.templateEdit
import io.joelt.texttemplate.ui.theme.TextTemplateTheme
import org.koin.androidx.compose.koinViewModel

fun Route.templatePreview(templateId: Long) = "templates/$templateId"

class TemplatePreviewController {
    var onEditTemplate = {}
    var onDeleteTemplate = {}
    var onCreateDraftWithTemplate = {}
}

class TemplatePreviewScreen : Screen {
    private val controller = TemplatePreviewController()

    override val route: String = "templates/{templateId}"
    override val arguments: List<NamedNavArgument> = listOf(
        navArgument("templateId") { type = NavType.LongType }
    )

    @Composable
    override fun scaffold(nav: NavHostController) = ScaffoldOptions(
        topBar = {
            TemplatePreviewTopNavBar(
                nav,
                onEditTemplate = { controller.onEditTemplate() },
                onDeleteTemplate = { controller.onDeleteTemplate() })
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(icon = {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.fill_up_template)
                )
            }, text = {
                Text(text = stringResource(R.string.fill_up_template))
            }, onClick = { controller.onCreateDraftWithTemplate() })
        }
    )

    @Composable
    override fun Composable(backStackEntry: NavBackStackEntry, nav: NavHostController) {
        val templateId = backStackEntry.arguments!!.getLong("templateId")
        TemplateEditScreen(nav, controller, templateId)
    }
}

@Composable
private fun TemplatePreviewScreenContent(
    template: Template?,
) {
    if (template == null) {
        Spacer(Modifier.height(32.dp))
        CircularProgressIndicator()
        return
    }

    TemplatePreview(template = template)
}

@Composable
private fun TemplateEditScreen(
    nav: NavHostController,
    screenController: TemplatePreviewController,
    templateId: Long,
    viewModel: TemplatePreviewViewModel = koinViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadTemplate(templateId)
        screenController.onEditTemplate = {
            nav.navigateClearStack(Route.templateEdit(templateId))
        }
        screenController.onDeleteTemplate = {
            viewModel.deleteTemplate(nav)
        }
        screenController.onCreateDraftWithTemplate = {
            nav.navigateClearStack(Route.createDraft(templateId))
        }
    }

    TemplatePreviewScreenContent(viewModel.template)
}

@Preview(showBackground = true)
@Composable
private fun TemplateEditScreenPreview() {
    val template = genTemplates(1)[0]

    val screen = TemplatePreviewScreen()
    val nav = rememberNavController()
    TextTemplateTheme {
        AppScaffold(scaffoldOptions = screen.scaffold(nav)) {
            TemplatePreviewScreenContent(template)
        }
    }
}
