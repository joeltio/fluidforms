package io.joelt.texttemplate.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import io.joelt.texttemplate.navigation.*
import io.joelt.texttemplate.ui.components.CardsColumn
import io.joelt.texttemplate.ui.components.TemplateRow
import io.joelt.texttemplate.ui.viewmodels.TemplatesViewModel
import org.koin.androidx.compose.koinViewModel

class TemplatesScreen : Screen {
    override fun route(): String = "templates"

    override fun arguments(): List<NamedNavArgument> = listOf()

    override fun makeComposable(backStackEntry: NavBackStackEntry): ScreenComposable =
        { nav, scaffold ->
            TemplatesScreen(nav, scaffold)
        }
}

@Composable
private fun TemplatesScreen(
    nav: NavHostController,
    scaffold: ScaffoldController,
    viewModel: TemplatesViewModel = koinViewModel()
) {
    scaffold.changeNavBars(ScaffoldType.HOME_SCREEN)

    CardsColumn(loading = viewModel.loading) {
        val templates = viewModel.templates!!
        LazyColumn {
            items(templates.size) {
                TemplateRow(templates[it], modifier = Modifier.fillMaxWidth()) {
                    nav.navigateToTemplateEdit(templates[it].id)
                }
                Divider()
            }
        }
    }
}
