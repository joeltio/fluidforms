package io.joelt.texttemplate.ui.screens.templates

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import io.joelt.texttemplate.AppScaffold
import io.joelt.texttemplate.R
import io.joelt.texttemplate.models.Template
import io.joelt.texttemplate.models.genTemplates
import io.joelt.texttemplate.navigation.*
import io.joelt.texttemplate.ui.components.TemplateList
import io.joelt.texttemplate.ui.screens.BottomNavBar
import io.joelt.texttemplate.ui.screens.TopNavBar
import io.joelt.texttemplate.ui.screens.template_edit.navigateToCreateTemplate
import io.joelt.texttemplate.ui.screens.template_preview.templatePreviewRoute
import io.joelt.texttemplate.ui.theme.TextTemplateTheme
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
class TemplatesScreen : Screen {
    override val route: String = "templates"
    override val arguments: List<NamedNavArgument> = emptyList()
    @Composable
    override fun scaffold(nav: NavHostController): ScaffoldOptions {
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        return ScaffoldOptions(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = { TopNavBar(nav, scrollBehavior) },
            bottomBar = { BottomNavBar(nav) },
            floatingActionButton = {
                FloatingActionButton(onClick = { nav.navigateToCreateTemplate() }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(id = R.string.add_template)
                    )
                }
            }
        )
    }

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
    LaunchedEffect(Unit) {
        viewModel.loadTemplates()
    }

    TemplatesScreenContent(templates = viewModel.templates) {
        nav.navigate(nav.templatePreviewRoute(it.id))
    }
}

@Preview(showBackground = true)
@Composable
private fun TemplatesScreenPreview() {
    val screen = TemplatesScreen()
    val nav = rememberNavController()
    TextTemplateTheme {
        AppScaffold(scaffoldOptions = screen.scaffold(nav)) {
            TemplatesScreenContent(templates = genTemplates(10)) {}
        }
    }
}
