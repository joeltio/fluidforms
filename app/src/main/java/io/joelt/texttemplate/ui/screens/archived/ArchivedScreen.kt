package io.joelt.texttemplate.ui.screens.archived

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import io.joelt.texttemplate.ui.screens.archived_view.archivedEdit
import org.koin.androidx.compose.koinViewModel

val Route.archived: String
    get() = "archived"

@OptIn(ExperimentalMaterial3Api::class)
class ArchivedScreen : Screen {
    override val route: String = Route.archived
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
        nav.navigate(Route.archivedEdit(it.id))
    }
}
