package io.joelt.texttemplate.ui.screens.templates

import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import io.joelt.texttemplate.models.Template
import io.joelt.texttemplate.navigation.*
import io.joelt.texttemplate.ui.components.TemplateList
import io.joelt.texttemplate.ui.screens.BottomNavBar
import io.joelt.texttemplate.ui.screens.TopNavBar
import io.joelt.texttemplate.ui.screens.draft_edit.navigateToCreateDraft
import org.koin.androidx.compose.koinViewModel

class TemplatesScreen : Screen {
    override val route: String = "templates"
    override val arguments: List<NamedNavArgument> = emptyList()
    override fun scaffold(nav: NavHostController) = ScaffoldOptions(
        topBar = { TopNavBar(nav) },
        bottomBar = { BottomNavBar(nav) }
    )

    @Composable
    override fun Composable(backStackEntry: NavBackStackEntry, nav: NavHostController) {
        TemplatesScreen(nav)
    }
}

@Composable
private fun TemplatesScreenContent(
    templates: List<Template>?,
    onTemplateClick: (Template) -> Unit
) {
    TemplateList(templates, onItemClick = onTemplateClick)
}

@Composable
private fun TemplatesScreen(
    nav: NavHostController,
    viewModel: TemplatesViewModel = koinViewModel()
) {
    TemplatesScreenContent(templates = viewModel.templates) {
        nav.navigateToCreateDraft(it.id)
    }
}
