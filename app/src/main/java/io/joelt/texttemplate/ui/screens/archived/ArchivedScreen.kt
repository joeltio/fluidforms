package io.joelt.texttemplate.ui.screens.archived

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import io.joelt.texttemplate.models.Draft
import io.joelt.texttemplate.navigation.*
import io.joelt.texttemplate.ui.components.DraftList
import io.joelt.texttemplate.ui.components.SimpleScaffold
import io.joelt.texttemplate.ui.components.TopNavBar
import io.joelt.texttemplate.ui.screens.BottomNavBar
import io.joelt.texttemplate.ui.screens.archived_view.navigateToArchivedEdit
import org.koin.androidx.compose.koinViewModel

class ArchivedScreen : Screen {
    override fun route(): String = "archived"

    override fun arguments(): List<NamedNavArgument> = listOf()

    @Composable
    override fun Composable(backStackEntry: NavBackStackEntry, nav: NavHostController) {
        ArchivedScreen(nav)
    }
}

@Composable
private fun ArchivedScreenContent(archived: List<Draft>?, onArchivedClick: (Draft) -> Unit) {
    DraftList(drafts = archived, onItemClick = onArchivedClick)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ArchivedScreen(
    nav: NavHostController,
    viewModel: ArchivedViewModel = koinViewModel()
) {
    SimpleScaffold(topBar = { TopNavBar(nav) }, bottomBar = { BottomNavBar(nav) }) {
        ArchivedScreenContent(archived = viewModel.archived) {
            nav.navigateToArchivedEdit(it.id)
        }
    }
}
