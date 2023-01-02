package io.joelt.fluidforms.ui.screens.forms

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
import io.joelt.fluidforms.AppScaffold
import io.joelt.fluidforms.R
import io.joelt.fluidforms.models.Form
import io.joelt.fluidforms.models.genForms
import io.joelt.fluidforms.navigation.*
import io.joelt.fluidforms.ui.components.FormList
import io.joelt.fluidforms.ui.screens.form_edit.createForm
import io.joelt.fluidforms.ui.screens.form_preview.formPreview
import io.joelt.fluidforms.ui.theme.FluidFormsTheme
import io.joelt.fluidforms.ui.theme.Typography
import org.koin.androidx.compose.koinViewModel

val Route.forms: String
    get() = "forms"

@Composable
private fun EmptyFormsMessage() {
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
private fun formsScreenContent(
    nav: NavHostController,
    forms: List<Form>?,
    onFormClick: (Form) -> Unit,
) = buildScreenContent {
    scaffoldOptions {
        floatingActionButton = {
            FloatingActionButton(onClick = { nav.navigate(Route.createForm) }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.home_new_form)
                )
            }
        }
    }

    content {
        if (forms?.size == 0) {
            EmptyFormsMessage()
        } else {
            FormList(forms, onItemClick = onFormClick)
        }
    }
}

val FormsScreen = buildScreen {
    route = Route.forms
    arguments = emptyList()

    contentFactory { _, nav ->
        val viewModel: FormsViewModel = koinViewModel()

        LaunchedEffect(Unit) {
            viewModel.loadForms()
        }

        formsScreenContent(
            nav = nav,
            forms = viewModel.forms,
            onFormClick = {
                nav.navigate(Route.formPreview(it.id))
            })
    }
}

@Preview(showBackground = true)
@Composable
private fun FormsScreenPreview() {
    val nav = rememberNavController()
    val screen = formsScreenContent(nav = nav, forms = genForms(10), onFormClick = {})
    FluidFormsTheme {
        AppScaffold(scaffoldOptions = screen.scaffoldOptions) {
            screen.content()
        }
    }
}
