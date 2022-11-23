package io.joelt.texttemplate.ui.screens.archived

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
import io.joelt.texttemplate.ui.screens.archived_view.navigateToArchivedEdit
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
class ArchivedScreen : Screen {
    override val route: String = "archived"
    override val arguments: List<NamedNavArgument> = emptyList()
    @Composable
    override fun scaffold(nav: NavHostController) = ScaffoldOptions(
        topBar = { TopNavBar(nav) },
        bottomBar = { BottomNavBar(nav) }
    )

    @Composable
    override fun Composable(backStackEntry: NavBackStackEntry, nav: NavHostController) {
        ArchivedScreen(nav)
    }
}

@Composable
private fun ArchivedScreenContent(archived: List<Draft>?, onArchivedClick: (Draft) -> Unit) {
    DraftList(drafts = archived, onItemClick = onArchivedClick)
}

@Composable
private fun ArchivedScreen(
    nav: NavHostController,
    viewModel: ArchivedViewModel = koinViewModel()
) {
    ArchivedScreenContent(archived = viewModel.archived) {
        nav.navigateToArchivedEdit(it.id)
    }
}
