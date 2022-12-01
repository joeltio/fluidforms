package io.joelt.texttemplate.ui.screens.template_preview

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.*
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

@Composable
private fun templatePreviewScreenContent(
    template: Template?,
    onBack: () -> Unit,
    onEditTemplate: () -> Unit,
    onDeleteTemplate: () -> Unit,
    onCreateDraftWithTemplate: () -> Unit
) = buildScreenContent {
    var showConfirmDeleteDialog by remember { mutableStateOf(false) }
    scaffoldOptions {
        topBar = {
            TemplatePreviewTopNavBar(
                onBack = onBack,
                onEditTemplate = onEditTemplate,
                onDeleteTemplate = { showConfirmDeleteDialog = true })
        }
        floatingActionButton = {
            ExtendedFloatingActionButton(icon = {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.fill_up_template)
                )
            }, text = {
                Text(text = stringResource(R.string.fill_up_template))
            }, onClick = onCreateDraftWithTemplate)
        }
    }

    content {
        if (showConfirmDeleteDialog) {
            AlertDialog(
                title = { Text(text = stringResource(R.string.template_confirm_delete_title)) },
                text = { Text(text = stringResource(R.string.template_confirm_delete)) },
                onDismissRequest = { showConfirmDeleteDialog = false },
                confirmButton = {
                    TextButton(onClick = onDeleteTemplate) {
                        Text(text = stringResource(R.string.dialog_delete))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmDeleteDialog = false }) {
                        Text(text = stringResource(R.string.dialog_cancel))
                    }
                }
            )
        }

        if (template == null) {
            Spacer(Modifier.height(32.dp))
            CircularProgressIndicator()
        } else {
            TemplatePreview(template = template)
        }
    }
}

val TemplatePreviewScreen = buildScreen {
    route = "templates/{templateId}"
    arguments = listOf(
        navArgument("templateId") { type = NavType.LongType }
    )

    contentFactory { backStackEntry, nav ->
        val templateId = backStackEntry.arguments!!.getLong("templateId")
        val viewModel: TemplatePreviewViewModel = koinViewModel()

        LaunchedEffect(Unit) {
            viewModel.loadTemplate(templateId)
        }

        templatePreviewScreenContent(
            template = viewModel.template,
            onBack = { nav.popBackStack() },
            onEditTemplate = { nav.navigateClearStack(Route.templateEdit(templateId)) },
            onDeleteTemplate = { viewModel.deleteTemplate(nav) },
            onCreateDraftWithTemplate = { nav.navigateClearStack(Route.createDraft(templateId)) })
    }
}

@Preview(showBackground = true)
@Composable
private fun TemplateEditScreenPreview() {
    val template = genTemplates(1)[0]

    val screen = templatePreviewScreenContent(template, {}, {}, {}, {})
    TextTemplateTheme {
        AppScaffold(scaffoldOptions = screen.scaffoldOptions) {
            screen.content()
        }
    }
}
