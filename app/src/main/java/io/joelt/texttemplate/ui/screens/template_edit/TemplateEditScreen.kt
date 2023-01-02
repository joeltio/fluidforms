package io.joelt.texttemplate.ui.screens.template_edit

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.*
import io.joelt.texttemplate.AppScaffold
import io.joelt.texttemplate.R
import io.joelt.texttemplate.models.Template
import io.joelt.texttemplate.models.genTemplates
import io.joelt.texttemplate.navigation.*
import io.joelt.texttemplate.ui.components.*
import io.joelt.texttemplate.ui.theme.TextTemplateTheme
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

fun Route.templateEdit(templateId: Long) = "templates/$templateId/edit"

val Route.createTemplate: String
    get() = "templates/0/edit"

data class TemplateEditState(
    val template: Template,
    val editorState: TemplateEditorState
) {
    constructor(
        template: Template
    ) : this(template, TemplateEditorState(template))

    fun withEditorState(editorState: TemplateEditorState) =
        copy(
            template = template.copy(
                name = editorState.templateName,
                body = editorState.editorState.templateBody
            ),
            editorState = editorState
        )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun templateEditScreenContent(
    state: TemplateEditState?,
    onStateChange: (TemplateEditState) -> Unit,
    hasTemplateChanged: () -> Boolean,
    onSave: () -> Unit,
    onBack: () -> Unit,
) = buildScreenContent {
    var showConfirmDiscardDialog by remember { mutableStateOf(false) }
    scaffoldOptions {
        topBar = {
            TemplateEditTopNavBar(
                onSave = onSave,
                onBack = {
                    if (hasTemplateChanged()) {
                        showConfirmDiscardDialog = true
                    } else {
                        onBack()
                    }
                }
            )
        }
    }

    content {
        if (showConfirmDiscardDialog) {
            AlertDialog(
                title = { Text(text = stringResource(R.string.template_edit_confirm_discard_title)) },
                text = { Text(text = stringResource(R.string.template_edit_confirm_discard_description)) },
                onDismissRequest = { showConfirmDiscardDialog = false },
                confirmButton = {
                    TextButton(onClick = onBack) {
                        Text(text = stringResource(R.string.all_dialog_discard))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmDiscardDialog = false }) {
                        Text(text = stringResource(R.string.all_dialog_cancel))
                    }
                }
            )
        }

        if (state == null) {
            Spacer(Modifier.height(32.dp))
            CircularProgressIndicator()
        } else {
            val focusRequester = remember { FocusRequester() }
            val kb = LocalSoftwareKeyboardController.current

            LaunchedEffect(focusRequester) {
                focusRequester.requestFocus()
                // This delay has to be here, not sure why
                // See: https://stackoverflow.com/questions/71412537/how-to-show-keyboard-with-jetpack-compose
                delay(100)
                kb?.show()

                onStateChange(state.withEditorState(state.editorState.moveCursorToEnd()))
            }

            TemplateEditor(
                bodyModifier = Modifier.focusRequester(focusRequester),
                state = state.editorState,
                onStateChange = { onStateChange(state.withEditorState(it)) })
        }
    }
}

val TemplateEditScreen = buildScreen {
    route = "templates/{templateId}/edit"
    arguments = listOf(
        navArgument("templateId") { type = NavType.LongType }
    )

    contentFactory { backStackEntry, nav ->
        val defaultName = stringResource(R.string.template_edit_new_template_name)
        val templateId = backStackEntry.arguments!!.getLong("templateId")
        val viewModel: TemplateEditViewModel = koinViewModel()

        LaunchedEffect(Unit) {
            // The LaunchedEffect gets run again when orientation changes
            // don't reload when the orientation changes
            if (viewModel.screenState == null) {
                viewModel.loadTemplate(templateId, defaultName)
            }
        }

        templateEditScreenContent(
            state = viewModel.screenState,
            onStateChange = { viewModel.screenState = it },
            hasTemplateChanged = { viewModel.templateChanged() },
            onSave = {
                viewModel.saveTemplate(nav)
            },
            onBack = {
                nav.popBackStack()
            })
    }
}

@Preview(showBackground = true)
@Composable
private fun TemplateEditScreenPreview() {
    val template = genTemplates(1)[0]
    var screenState by remember { mutableStateOf(TemplateEditState(template)) }

    val screen = templateEditScreenContent(
        state = screenState,
        onStateChange = { screenState = it },
        hasTemplateChanged = { true },
        onSave = {},
        onBack = {})
    TextTemplateTheme {
        AppScaffold(scaffoldOptions = screen.scaffoldOptions) {
            screen.content()
        }
    }
}
