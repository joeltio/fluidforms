package io.joelt.texttemplate.ui.screens.archived_preview

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.*
import io.joelt.texttemplate.AppScaffold
import io.joelt.texttemplate.R
import io.joelt.texttemplate.models.Draft
import io.joelt.texttemplate.models.genTemplates
import io.joelt.texttemplate.navigation.*
import io.joelt.texttemplate.ui.components.TemplatePreview
import io.joelt.texttemplate.ui.theme.TextTemplateTheme
import org.koin.androidx.compose.koinViewModel

fun Route.archivedPreview(archivedId: Long) = "archived/$archivedId"

@Composable
private fun archivedPreviewScreenContent(
    archived: Draft?,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) = buildScreenContent {
    var showConfirmDeleteDialog by remember { mutableStateOf(false) }
    scaffoldOptions {
        topBar = {
            ArchivedPreviewTopNavBar(
                onBack = onBack,
                onDeleteArchived = { showConfirmDeleteDialog = true })
        }
        floatingActionButton = {
            ExtendedFloatingActionButton(icon = {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.archived_preview_edit_archived)
                )
            }, text = {
                Text(text = stringResource(R.string.archived_preview_edit_archived))
            }, onClick = onEdit)
        }
    }

    content {
        if (showConfirmDeleteDialog) {
            AlertDialog(
                title = { Text(text = stringResource(R.string.archived_preview_confirm_delete_title)) },
                text = { Text(text = stringResource(R.string.archived_preview_confirm_delete_description)) },
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

        if (archived == null) {
            Spacer(Modifier.height(32.dp))
            CircularProgressIndicator()
        } else {
            TemplatePreview(name = archived.name, body = archived.body)
        }
    }
}

val ArchivedPreviewScreen = buildScreen {
    route = "archived/{archivedId}"
    arguments = listOf(
        navArgument("archivedId") { type = NavType.LongType }
    )

    contentFactory { backStackEntry, nav ->
        val archivedId = backStackEntry.arguments!!.getLong("archivedId")
        val viewModel: ArchivedPreviewViewModel = koinViewModel()

        LaunchedEffect(Unit) {
            viewModel.loadArchived(archivedId)
        }

        archivedPreviewScreenContent(
            archived = viewModel.archived,
            onBack = { nav.popBackStack() },
            onDelete = { viewModel.deleteArchived(nav) },
            onEdit = { viewModel.unarchive(nav) })
    }
}

@Preview(showBackground = true)
@Composable
private fun ArchivedPreviewScreenPreview() {
    val template = genTemplates(1)[0]
    val archived = Draft.fromTemplate(template)

    val screen = archivedPreviewScreenContent(archived, {}, {}, {})
    TextTemplateTheme {
        AppScaffold(scaffoldOptions = screen.scaffoldOptions) {
            screen.content()
        }
    }
}
