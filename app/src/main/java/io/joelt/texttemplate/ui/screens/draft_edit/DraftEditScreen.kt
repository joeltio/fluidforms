package io.joelt.texttemplate.ui.screens.draft_edit

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation.*
import io.joelt.texttemplate.R
import io.joelt.texttemplate.models.Draft
import io.joelt.texttemplate.models.Either
import io.joelt.texttemplate.navigation.*
import io.joelt.texttemplate.ui.components.DraftEditor
import io.joelt.texttemplate.ui.components.DraftEditorState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

fun Route.draftEdit(draftId: Long) = "drafts/$draftId/edit/0"

fun Route.createDraft(fromTemplateId: Long) = "drafts/0/edit/$fromTemplateId"

private class DraftEditController {
    var snackbarHostState: SnackbarHostState? = null
    var onSave = {}
    var onCopyToClipboard = {}
}

class DraftEditScreen : Screen {
    private val controller = DraftEditController()

    override val route: String = "drafts/{draftId}/edit/{templateId}"
    override val arguments: List<NamedNavArgument> = listOf(
        navArgument("draftId") { type = NavType.LongType },
        navArgument("templateId") { type = NavType.LongType }
    )

    @Composable
    override fun scaffold(nav: NavHostController): ScaffoldOptions {
        val snackbarHostState = remember { SnackbarHostState() }
        controller.snackbarHostState = snackbarHostState

        return ScaffoldOptions(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                DraftEditTopNavBar(
                    nav,
                    onSave = { controller.onSave() },
                    onCopyToClipboard = { controller.onCopyToClipboard() })
            },
        )
    }

    @Composable
    override fun Composable(backStackEntry: NavBackStackEntry, nav: NavHostController) {
        val draftId = backStackEntry.arguments!!.getLong("draftId")
        val templateId = backStackEntry.arguments!!.getLong("templateId")
        DraftEditScreen(nav, controller, draftId, templateId)
    }
}

@Composable
private fun DraftEditScreenContent(draft: Draft?, onDraftChange: (Draft) -> Unit) {
    if (draft == null) {
        Spacer(Modifier.height(32.dp))
        CircularProgressIndicator()
        return
    }

    DraftEditor(state = DraftEditorState(draft), onStateChange = {
        onDraftChange(draft.copy(name = it.name, slots = it.slots))
    })
}

@Composable
private fun DraftEditScreen(
    nav: NavHostController,
    screenController: DraftEditController,
    draftId: Long,
    templateId: Long,
    viewModel: DraftEditViewModel = koinViewModel()
) {
    val clipboardManager = LocalClipboardManager.current
    val clipboardMessage = stringResource(R.string.text_copied_to_clipboard)
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (draftId != 0L) {
            viewModel.loadDraft(draftId)
        } else if (templateId != 0L) {
            viewModel.createDraft(templateId)
        }

        screenController.onSave = {
            viewModel.saveDraft(nav)
        }

        screenController.onCopyToClipboard = {
            viewModel.draft?.let { draft ->
                val text = draft.slots.joinToString {
                    when (it) {
                        is Either.Left -> it.value
                        is Either.Right -> it.value.toDisplayString()
                    }
                }

                clipboardManager.setText(AnnotatedString(text))
                scope.launch {
                    screenController.snackbarHostState?.showSnackbar(clipboardMessage)
                }
            }
        }
    }

    DraftEditScreenContent(viewModel.draft) {
        viewModel.draft = it
    }
}
