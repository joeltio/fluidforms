package io.joelt.texttemplate.ui.screens.drafts

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
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
class DraftsScreen : Screen {
    override val route: String = Route.drafts
    override val arguments: List<NamedNavArgument> = emptyList()

    @Composable
    override fun scaffold(nav: NavHostController): ScaffoldOptions {
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        return ScaffoldOptions(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = { TopNavBar(nav, scrollBehavior) },
            bottomBar = { BottomNavBar(nav) }
        )
    }

    @Composable
    override fun Composable(backStackEntry: NavBackStackEntry, nav: NavHostController) {
        DraftsScreen(nav)
    }
}

@Composable
private fun DraftsScreenContent(
    drafts: List<Draft>?,
    onDraftClick: (Draft) -> Unit
) {
    DraftList(drafts, onItemClick = onDraftClick)
}

@Composable
private fun DraftsScreen(
    nav: NavHostController,
    viewModel: DraftsViewModel = koinViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadDrafts()
    }

    DraftsScreenContent(viewModel.drafts) {
        nav.navigate(Route.draftEdit(it.id))
    }
}
