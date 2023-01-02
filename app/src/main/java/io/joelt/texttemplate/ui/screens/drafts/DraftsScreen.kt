package io.joelt.texttemplate.ui.screens.drafts

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.joelt.texttemplate.R
import io.joelt.texttemplate.models.Draft
import io.joelt.texttemplate.navigation.*
import io.joelt.texttemplate.ui.components.DraftList
import io.joelt.texttemplate.ui.screens.draft_edit.draftEdit
import io.joelt.texttemplate.ui.theme.Typography
import org.koin.androidx.compose.koinViewModel

val Route.drafts: String
    get() = "drafts"

@Composable
private fun EmptyDraftsMessage() {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = stringResource(R.string.drafts_empty_title), style = Typography.headlineSmall, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = stringResource(R.string.drafts_empty_description), style = Typography.bodyMedium, textAlign = TextAlign.Center)
    }
}

@Composable
private fun draftsScreenContent(
    drafts: List<Draft>?,
    onDraftClick: (Draft) -> Unit
) = buildScreenContent {
    content {
        if (drafts?.size == 0) {
            EmptyDraftsMessage()
        } else {
            DraftList(drafts, onItemClick = onDraftClick)
        }
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
