package io.joelt.texttemplate.ui.screens.archived

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.joelt.texttemplate.R
import io.joelt.texttemplate.models.Draft
import io.joelt.texttemplate.navigation.*
import io.joelt.texttemplate.ui.components.DraftList
import io.joelt.texttemplate.ui.screens.archived_preview.archivedPreview
import io.joelt.texttemplate.ui.theme.Typography
import org.koin.androidx.compose.koinViewModel

val Route.archived: String
    get() = "archived"

@Composable
private fun EmptyArchivedMessage() {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = stringResource(R.string.empty_archived_title), style = Typography.headlineSmall, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = stringResource(R.string.empty_archived_description), style = Typography.bodyMedium, textAlign = TextAlign.Center)
    }
}

@Composable
private fun archivedScreenContent(
    archived: List<Draft>?,
    onArchivedClick: (Draft) -> Unit
) = buildScreenContent {
    content {
        if (archived?.size == 0) {
            EmptyArchivedMessage()
        } else {
            DraftList(drafts = archived, onItemClick = onArchivedClick)
        }
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
