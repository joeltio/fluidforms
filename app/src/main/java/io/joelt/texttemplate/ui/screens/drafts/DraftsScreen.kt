package io.joelt.texttemplate.ui.screens.drafts

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
import io.joelt.texttemplate.ui.screens.draft_edit.draftEdit
import org.koin.androidx.compose.koinViewModel

val Route.drafts: String
    get() = "drafts"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun draftsScreenContent(
    nav: NavHostController,
    drafts: List<Draft>?,
    onDraftClick: (Draft) -> Unit
) = buildScreenContent {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    scaffoldOptions {
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        topBar = { TopNavBar(nav, scrollBehavior) }
        bottomBar = { BottomNavBar(nav) }
    }

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
            nav = nav,
            drafts = viewModel.drafts,
            onDraftClick = { nav.navigate(Route.draftEdit(it.id)) })
    }
}
