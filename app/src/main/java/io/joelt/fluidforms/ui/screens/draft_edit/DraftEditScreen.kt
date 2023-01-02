package io.joelt.fluidforms.ui.screens.draft_edit

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation.*
import io.joelt.fluidforms.R
import io.joelt.fluidforms.models.Draft
import io.joelt.fluidforms.models.Either
import io.joelt.fluidforms.navigation.*
import io.joelt.fluidforms.ui.components.DraftEditor
import io.joelt.fluidforms.ui.components.DraftEditorState
import io.joelt.fluidforms.ui.screens.drafts.drafts
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

fun Route.draftEdit(draftId: Long) = "drafts/$draftId/edit/0"

fun Route.createDraft(fromFormId: Long) = "drafts/0/edit/$fromFormId"

fun NavHostController.navigateBackToDrafts() {
    navigate(Route.drafts) {
        this@navigateBackToDrafts.graph.startDestinationRoute?.let { route ->
            popUpTo(route)
        }
    }
}

@Composable
private fun draftEditScreenContent(
    draft: Draft?,
    onBack: () -> Unit,
    onDraftChange: (Draft) -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    onCopyToClipboard: () -> String?,
) = buildScreenContent {
    val snackbarHostState = remember { SnackbarHostState() }
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val clipboardMessage = stringResource(R.string.draft_edit_snackbar_copied_to_clipboard)
    var showConfirmDeleteDialog by remember { mutableStateOf(false) }

    scaffoldOptions {
        snackbarHost = { SnackbarHost(snackbarHostState) }
        topBar = {
            DraftEditTopNavBar(
                onBack = onBack,
                onSave = onSave,
                onDelete = { showConfirmDeleteDialog = true },
                onCopyToClipboard = {
                    val content = onCopyToClipboard()
                    content?.let {
                        clipboardManager.setText(AnnotatedString(content))
                        scope.launch {
                            snackbarHostState.showSnackbar(clipboardMessage)
                        }
                    }
                })
        }
    }

    content {
        if (showConfirmDeleteDialog) {
            AlertDialog(
                title = { Text(text = stringResource(R.string.draft_edit_confirm_delete_title)) },
                text = { Text(text = stringResource(R.string.draft_edit_confirm_delete_description)) },
                onDismissRequest = { showConfirmDeleteDialog = false },
                confirmButton = {
                    TextButton(onClick = onDelete) {
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

        if (draft == null) {
            Spacer(Modifier.height(32.dp))
            CircularProgressIndicator()
        } else {
            DraftEditor(state = DraftEditorState(draft), onStateChange = {
                onDraftChange(draft.copy(name = it.name, body = it.body))
            })
        }
    }
}

val DraftEditScreen = buildScreen {
    route = "drafts/{draftId}/edit/{formId}"
    arguments = listOf(
        navArgument("draftId") { type = NavType.LongType },
        navArgument("formId") { type = NavType.LongType }
    )

    contentFactory { backStackEntry, nav ->
        val draftId = backStackEntry.arguments!!.getLong("draftId")
        val formId = backStackEntry.arguments!!.getLong("formId")
        val viewModel: DraftEditViewModel = koinViewModel()

        LaunchedEffect(Unit) {
            if (draftId != 0L) {
                viewModel.loadDraft(draftId)
            } else if (formId != 0L) {
                viewModel.createDraft(formId)
            }
        }

        BackHandler(enabled = true) {
            nav.navigateBackToDrafts()
        }

        draftEditScreenContent(
            draft = viewModel.draft,
            onBack = { nav.navigateBackToDrafts() },
            onDraftChange = { viewModel.draft = it },
            onSave = { viewModel.saveDraft(nav) },
            onDelete = { viewModel.deleteDraft(nav) },
            onCopyToClipboard = {
                viewModel.draft?.let { draft ->
                    draft.body.joinToString {
                        when (it) {
                            is Either.Left -> it.value
                            is Either.Right -> it.value.displayDefault()
                        }
                    }
                }
            }
        )
    }
}
