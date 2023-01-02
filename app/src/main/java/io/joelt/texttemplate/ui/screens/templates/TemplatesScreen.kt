package io.joelt.texttemplate.ui.screens.templates

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import io.joelt.texttemplate.AppScaffold
import io.joelt.texttemplate.R
import io.joelt.texttemplate.models.Template
import io.joelt.texttemplate.models.genTemplates
import io.joelt.texttemplate.navigation.*
import io.joelt.texttemplate.ui.components.TemplateList
import io.joelt.texttemplate.ui.screens.template_edit.createTemplate
import io.joelt.texttemplate.ui.screens.template_preview.templatePreview
import io.joelt.texttemplate.ui.theme.TextTemplateTheme
import io.joelt.texttemplate.ui.theme.Typography
import org.koin.androidx.compose.koinViewModel

val Route.templates: String
    get() = "templates"

@Composable
private fun EmptyTemplatesMessage() {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = stringResource(R.string.forms_empty_title), style = Typography.headlineSmall, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = stringResource(R.string.forms_empty_description), style = Typography.bodyMedium, textAlign = TextAlign.Center)
    }
}

@Composable
private fun templatesScreenContent(
    nav: NavHostController,
    templates: List<Template>?,
    onTemplateClick: (Template) -> Unit,
) = buildScreenContent {
    scaffoldOptions {
        floatingActionButton = {
            FloatingActionButton(onClick = { nav.navigate(Route.createTemplate) }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.home_new_form)
                )
            }
        }
    }

    content {
        if (templates?.size == 0) {
            EmptyTemplatesMessage()
        } else {
            TemplateList(templates, onItemClick = onTemplateClick)
        }
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
