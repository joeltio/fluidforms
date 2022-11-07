package io.joelt.texttemplate.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import io.joelt.texttemplate.navigation.*
import io.joelt.texttemplate.ui.components.TemplateList
import io.joelt.texttemplate.ui.viewmodels.TemplatesViewModel
import org.koin.androidx.compose.koinViewModel

class TemplatesScreen : Screen {
    override fun route(): String = "templates"

    override fun arguments(): List<NamedNavArgument> = listOf()

    override fun makeComposable(backStackEntry: NavBackStackEntry): ScreenComposable =
        { nav, scaffold ->
            scaffold.changeNavBars(ScaffoldType.HOME_SCREEN)
            TemplatesScreen(nav, scaffold)
        }
}

@Composable
private fun TemplatesScreen(
    nav: NavHostController,
    scaffold: ScaffoldController,
    viewModel: TemplatesViewModel = koinViewModel()
) {
    TemplateList(templates = viewModel.templates) {
        nav.navigateToTemplateEdit(it.id)
    }
}
