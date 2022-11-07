package io.joelt.texttemplate.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import io.joelt.texttemplate.navigation.*
import io.joelt.texttemplate.ui.components.DraftList
import io.joelt.texttemplate.ui.viewmodels.DraftsViewModel
import org.koin.androidx.compose.koinViewModel

class DraftsScreen : Screen {
    override fun route(): String = "drafts"

    override fun arguments(): List<NamedNavArgument> = listOf()

    override fun makeComposable(backStackEntry: NavBackStackEntry): ScreenComposable =
        { nav, scaffold ->
            scaffold.changeNavBars(ScaffoldType.HOME_SCREEN)
            DraftsScreen(nav, scaffold)
        }
}

@Composable
private fun DraftsScreen(
    nav: NavHostController,
    scaffold: ScaffoldController,
    viewModel: DraftsViewModel = koinViewModel()
) {
    DraftList(drafts = viewModel.drafts) {
        nav.navigateToDraftEdit(it.id)
    }
}
