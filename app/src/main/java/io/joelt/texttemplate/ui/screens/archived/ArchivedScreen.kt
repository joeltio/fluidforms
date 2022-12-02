package io.joelt.texttemplate.ui.screens.archived

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import io.joelt.texttemplate.models.Draft
import io.joelt.texttemplate.navigation.*
import io.joelt.texttemplate.ui.components.DraftList
import io.joelt.texttemplate.ui.screens.archived_preview.archivedPreview
import org.koin.androidx.compose.koinViewModel

val Route.archived: String
    get() = "archived"

@Composable
private fun archivedScreenContent(
    archived: List<Draft>?,
    onArchivedClick: (Draft) -> Unit
) = buildScreenContent {
    content {
        DraftList(drafts = archived, onItemClick = onArchivedClick)
    }
}

val ArchivedScreen = buildScreen {
    route = Route.archived
    arguments = emptyList()

    contentFactory { _, nav ->
        val viewModel: ArchivedViewModel = koinViewModel()

        LaunchedEffect(Unit) {
            viewModel.loadArchived()
        }

        archivedScreenContent(
            archived = viewModel.archived,
            onArchivedClick = { nav.navigate(Route.archivedPreview(it.id)) })
    }
}
