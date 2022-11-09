package io.joelt.texttemplate.ui.screens.drafts

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import io.joelt.texttemplate.models.Draft
import io.joelt.texttemplate.navigation.*
import io.joelt.texttemplate.ui.components.DraftList
import io.joelt.texttemplate.ui.components.SimpleScaffold
import io.joelt.texttemplate.ui.components.TopNavBar
import io.joelt.texttemplate.ui.screens.BottomNavBar
import io.joelt.texttemplate.ui.screens.draft_edit.navigateToDraftEdit
import org.koin.androidx.compose.koinViewModel

class DraftsScreen : Screen {
    override fun route(): String = "drafts"

    override fun arguments(): List<NamedNavArgument> = listOf()

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DraftsScreen(
    nav: NavHostController,
    viewModel: DraftsViewModel = koinViewModel()
) {
    SimpleScaffold(topBar = { TopNavBar(nav) }, bottomBar = { BottomNavBar(nav) }) {
        DraftsScreenContent(drafts = viewModel.drafts) {
            nav.navigateToDraftEdit(it.id)
        }
    }
}
