package io.joelt.texttemplate.ui.screens.templates

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import io.joelt.texttemplate.models.Template
import io.joelt.texttemplate.navigation.*
import io.joelt.texttemplate.ui.components.SimpleScaffold
import io.joelt.texttemplate.ui.components.TemplateList
import io.joelt.texttemplate.ui.components.TopNavBar
import io.joelt.texttemplate.ui.screens.BottomNavBar
import io.joelt.texttemplate.ui.screens.draft_edit.navigateToCreateDraft
import org.koin.androidx.compose.koinViewModel

class TemplatesScreen : Screen {
    override fun route(): String = "templates"

    override fun arguments(): List<NamedNavArgument> = listOf()

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TemplatesScreen(
    nav: NavHostController,
    viewModel: TemplatesViewModel = koinViewModel()
) {
    SimpleScaffold(topBar = { TopNavBar(nav) }, bottomBar = { BottomNavBar(nav) }) {
        TemplatesScreenContent(templates = viewModel.templates) {
            nav.navigateToCreateDraft(it.id)
        }
    }
}
