package io.joelt.fluidforms.ui.screens.form_preview

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
import io.joelt.fluidforms.AppScaffold
import io.joelt.fluidforms.R
import io.joelt.fluidforms.models.Form
import io.joelt.fluidforms.models.genForms
import io.joelt.fluidforms.navigation.*
import io.joelt.fluidforms.ui.components.FormPreview
import io.joelt.fluidforms.ui.screens.draft_edit.createDraft
import io.joelt.fluidforms.ui.screens.form_edit.formEdit
import io.joelt.fluidforms.ui.screens.forms.forms
import io.joelt.fluidforms.ui.theme.FluidFormsTheme
import org.koin.androidx.compose.koinViewModel

fun Route.formPreview(formId: Long) = "forms/$formId"

@Composable
private fun formPreviewScreenContent(
    form: Form?,
    onBack: () -> Unit,
    onEditForm: () -> Unit,
    onDeleteForm: () -> Unit,
    onCreateDraftWithForm: () -> Unit
) = buildScreenContent {
    var showConfirmDeleteDialog by remember { mutableStateOf(false) }
    scaffoldOptions {
        topBar = {
            FormPreviewTopNavBar(
                onBack = onBack,
                onEditForm = onEditForm,
                onDeleteForm = { showConfirmDeleteDialog = true })
        }
        floatingActionButton = {
            ExtendedFloatingActionButton(icon = {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.form_preview_fill_form)
                )
            }, text = {
                Text(text = stringResource(R.string.form_preview_fill_form))
            }, onClick = onCreateDraftWithForm)
        }
    }

    content {
        if (showConfirmDeleteDialog) {
            AlertDialog(
                title = { Text(text = stringResource(R.string.form_preview_confirm_delete_title)) },
                text = { Text(text = stringResource(R.string.form_preview_confirm_delete_description)) },
                onDismissRequest = { showConfirmDeleteDialog = false },
                confirmButton = {
                    TextButton(onClick = onDeleteForm) {
                        Text(text = stringResource(R.string.all_dialog_delete))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmDeleteDialog = false }) {
                        Text(text = stringResource(R.string.all_dialog_cancel))
                    }
                }
            )
        }

        if (form == null) {
            Spacer(Modifier.height(32.dp))
            CircularProgressIndicator()
        } else {
            FormPreview(form = form)
        }
    }
}

val FormPreviewScreen = buildScreen {
    route = "forms/{formId}"
    arguments = listOf(
        navArgument("formId") { type = NavType.LongType }
    )

    contentFactory { backStackEntry, nav ->
        val formId = backStackEntry.arguments!!.getLong("formId")
        val viewModel: FormPreviewViewModel = koinViewModel()

        LaunchedEffect(Unit) {
            viewModel.loadForm(formId)
        }

        formPreviewScreenContent(
            form = viewModel.form,
            onBack = { nav.navigateClearStack(Route.forms) },
            onEditForm = { nav.navigateClearStack(Route.formEdit(formId)) },
            onDeleteForm = { viewModel.deleteForm(nav) },
            onCreateDraftWithForm = { nav.navigateClearStack(Route.createDraft(formId)) })
    }
}

@Preview(showBackground = true)
@Composable
private fun FormEditScreenPreview() {
    val form = genForms(1)[0]

    val screen = formPreviewScreenContent(form, {}, {}, {}, {})
    FluidFormsTheme {
        AppScaffold(scaffoldOptions = screen.scaffoldOptions) {
            screen.content()
        }
    }
}
