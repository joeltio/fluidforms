package io.joelt.fluidforms.ui.screens.form_edit

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
import io.joelt.fluidforms.AppScaffold
import io.joelt.fluidforms.R
import io.joelt.fluidforms.models.Form
import io.joelt.fluidforms.models.genForms
import io.joelt.fluidforms.navigation.*
import io.joelt.fluidforms.ui.components.*
import io.joelt.fluidforms.ui.theme.FluidFormsTheme
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

fun Route.formEdit(formId: Long) = "forms/$formId/edit"

val Route.createForm: String
    get() = "forms/0/edit"

data class FormEditState(
    val form: Form,
    val editorState: FormEditorState
) {
    constructor(
        form: Form
    ) : this(form, FormEditorState(form))

    fun withEditorState(editorState: FormEditorState) =
        copy(
            form = form.copy(
                name = editorState.formName,
                body = editorState.editorState.formBody
            ),
            editorState = editorState
        )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun formEditScreenContent(
    state: FormEditState?,
    onStateChange: (FormEditState) -> Unit,
    hasFormChanged: () -> Boolean,
    onSave: () -> Unit,
    onBack: () -> Unit,
) = buildScreenContent {
    var showConfirmDiscardDialog by remember { mutableStateOf(false) }
    scaffoldOptions {
        topBar = {
            FormEditTopNavBar(
                onSave = onSave,
                onBack = {
                    if (hasFormChanged()) {
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
                title = { Text(text = stringResource(R.string.form_edit_confirm_discard_title)) },
                text = { Text(text = stringResource(R.string.form_edit_confirm_discard_description)) },
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

            FormEditor(
                bodyModifier = Modifier.focusRequester(focusRequester),
                state = state.editorState,
                onStateChange = { onStateChange(state.withEditorState(it)) })
        }
    }
}

val FormEditScreen = buildScreen {
    route = "forms/{formId}/edit"
    arguments = listOf(
        navArgument("formId") { type = NavType.LongType }
    )

    contentFactory { backStackEntry, nav ->
        val defaultName = stringResource(R.string.form_edit_new_form_name)
        val formId = backStackEntry.arguments!!.getLong("formId")
        val viewModel: FormEditViewModel = koinViewModel()

        LaunchedEffect(Unit) {
            // The LaunchedEffect gets run again when orientation changes
            // don't reload when the orientation changes
            if (viewModel.screenState == null) {
                viewModel.loadForm(formId, defaultName)
            }
        }

        formEditScreenContent(
            state = viewModel.screenState,
            onStateChange = { viewModel.screenState = it },
            hasFormChanged = { viewModel.formChanged() },
            onSave = {
                viewModel.saveForm(nav)
            },
            onBack = {
                nav.popBackStack()
            })
    }
}

@Preview(showBackground = true)
@Composable
private fun FormEditScreenPreview() {
    val form = genForms(1)[0]
    var screenState by remember { mutableStateOf(FormEditState(form)) }

    val screen = formEditScreenContent(
        state = screenState,
        onStateChange = { screenState = it },
        hasFormChanged = { true },
        onSave = {},
        onBack = {})
    FluidFormsTheme {
        AppScaffold(scaffoldOptions = screen.scaffoldOptions) {
            screen.content()
        }
    }
}
