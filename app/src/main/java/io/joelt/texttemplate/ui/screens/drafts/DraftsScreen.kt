package io.joelt.texttemplate.ui.screens.drafts

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import io.joelt.texttemplate.models.Draft
import io.joelt.texttemplate.navigation.*
import io.joelt.texttemplate.ui.components.DraftList
import io.joelt.texttemplate.ui.screens.TopNavBar
import io.joelt.texttemplate.ui.screens.BottomNavBar
import io.joelt.texttemplate.ui.screens.draft_edit.navigateToDraftEdit
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
class DraftsScreen : Screen {
    override val route: String = "drafts"
    override val arguments: List<NamedNavArgument> = emptyList()

    @Composable
    override fun scaffold(nav: NavHostController) = ScaffoldOptions(
        topBar = { TopNavBar(nav) },
        bottomBar = { BottomNavBar(nav) }
    )

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
    DraftsScreenContent(drafts = viewModel.drafts) {
        nav.navigateToDraftEdit(it.id)
    }
}
