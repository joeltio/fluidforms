package io.joelt.texttemplate.ui.screens.drafts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import io.joelt.texttemplate.models.Draft
import io.joelt.texttemplate.navigation.*
import io.joelt.texttemplate.ui.components.DraftList
import io.joelt.texttemplate.ui.screens.draft_edit.draftEdit
import org.koin.androidx.compose.koinViewModel

val Route.drafts: String
    get() = "drafts"

@Composable
private fun draftsScreenContent(
    drafts: List<Draft>?,
    onDraftClick: (Draft) -> Unit
) = buildScreenContent {
    content {
        DraftList(drafts, onItemClick = onDraftClick)
    }
}

val DraftsScreen = buildScreen {
    route = Route.drafts
    arguments = emptyList()

    contentFactory { _, nav ->
        val viewModel: DraftsViewModel = koinViewModel()
        LaunchedEffect(Unit) {
            viewModel.loadDrafts()
        }

        draftsScreenContent(
            drafts = viewModel.drafts,
            onDraftClick = { nav.navigate(Route.draftEdit(it.id)) })
    }
}
