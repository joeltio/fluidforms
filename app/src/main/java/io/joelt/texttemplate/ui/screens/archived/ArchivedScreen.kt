package io.joelt.texttemplate.ui.screens.archived

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.NavHostController
import io.joelt.texttemplate.models.Draft
import io.joelt.texttemplate.navigation.*
import io.joelt.texttemplate.ui.components.DraftList
import io.joelt.texttemplate.ui.screens.TopNavBar
import io.joelt.texttemplate.ui.screens.BottomNavBar
import io.joelt.texttemplate.ui.screens.archived_preview.archivedPreview
import org.koin.androidx.compose.koinViewModel

val Route.archived: String
    get() = "archived"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun archivedScreenContent(
    nav: NavHostController,
    archived: List<Draft>?,
    onArchivedClick: (Draft) -> Unit
) = buildScreenContent {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    scaffoldOptions {
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        topBar = { TopNavBar(nav, scrollBehavior) }
        bottomBar = { BottomNavBar(nav) }
    }

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
            nav = nav,
            archived = viewModel.archived,
            onArchivedClick = { nav.navigate(Route.archivedPreview(it.id)) })
    }
}
