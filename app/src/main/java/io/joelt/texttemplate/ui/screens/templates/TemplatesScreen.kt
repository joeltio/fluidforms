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
import io.joelt.texttemplate.ui.screens.template_edit.createTemplate
import io.joelt.texttemplate.ui.screens.template_preview.templatePreview
import io.joelt.texttemplate.ui.theme.TextTemplateTheme
import org.koin.androidx.compose.koinViewModel

val Route.templates: String
    get() = "templates"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun templatesScreenContent(
    nav: NavHostController,
    templates: List<Template>?,
    onTemplateClick: (Template) -> Unit,
) = buildScreenContent {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    scaffoldOptions {
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        topBar = { TopNavBar(nav, scrollBehavior) }
        bottomBar = { BottomNavBar(nav) }
        floatingActionButton = {
            FloatingActionButton(onClick = { nav.navigate(Route.createTemplate) }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.add_template)
                )
            }
        }
    }

    content {
        TemplateList(templates, onItemClick = onTemplateClick)
    }
}

val TemplatesScreen = buildScreen {
    route = Route.templates
    arguments = emptyList()

    contentFactory { _, nav ->
        val viewModel: TemplatesViewModel = koinViewModel()

        LaunchedEffect(Unit) {
            viewModel.loadTemplates()
        }

        templatesScreenContent(
            nav = nav,
            templates = viewModel.templates,
            onTemplateClick = {
                nav.navigate(Route.templatePreview(it.id))
            })
    }
}

@Preview(showBackground = true)
@Composable
private fun TemplatesScreenPreview() {
    val nav = rememberNavController()
    val screen = templatesScreenContent(nav = nav, templates = genTemplates(10), onTemplateClick = {})
    TextTemplateTheme {
        AppScaffold(scaffoldOptions = screen.scaffoldOptions) {
            screen.content()
        }
    }
}
