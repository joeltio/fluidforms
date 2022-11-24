package io.joelt.texttemplate.ui.screens.archived_preview

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.*
import androidx.navigation.compose.rememberNavController
import io.joelt.texttemplate.AppScaffold
import io.joelt.texttemplate.R
import io.joelt.texttemplate.models.Draft
import io.joelt.texttemplate.models.genTemplates
import io.joelt.texttemplate.navigation.*
import io.joelt.texttemplate.ui.components.TemplatePreview
import io.joelt.texttemplate.ui.theme.TextTemplateTheme
import org.koin.androidx.compose.koinViewModel

fun Route.archivedPreview(archivedId: Long) = "archived/$archivedId"

class ArchivedPreviewController {
    var onEditArchived = {}
    var onDeleteArchived = {}
}

class ArchivedPreviewScreen : Screen {
    private val controller = ArchivedPreviewController()

    override val route: String = "archived/{archivedId}"
    override val arguments: List<NamedNavArgument> = listOf(
        navArgument("archivedId") { type = NavType.LongType }
    )

    @Composable
    override fun scaffold(nav: NavHostController) = ScaffoldOptions(
        topBar = {
            ArchivedPreviewTopNavBar(
                nav,
                onDeleteArchived = { controller.onDeleteArchived() })
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(icon = {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.fill_up_archived)
                )
            }, text = {
                Text(text = stringResource(R.string.fill_up_archived))
            }, onClick = { controller.onEditArchived() })
        }
    )

    @Composable
    override fun Composable(backStackEntry: NavBackStackEntry, nav: NavHostController) {
        val archivedId = backStackEntry.arguments!!.getLong("archivedId")
        ArchivedPreviewScreen(nav, controller, archivedId)
    }
}

@Composable
private fun ArchivedPreviewScreenContent(
    archived: Draft?,
) {
    if (archived == null) {
        Spacer(Modifier.height(32.dp))
        CircularProgressIndicator()
        return
    }

    TemplatePreview(name = archived.name, slots = archived.slots)
}

@Composable
private fun ArchivedPreviewScreen(
    nav: NavHostController,
    screenController: ArchivedPreviewController,
    archivedId: Long,
    viewModel: ArchivedPreviewViewModel = koinViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadArchived(archivedId)
        screenController.onEditArchived = {
            viewModel.unarchive(nav)
        }
        screenController.onDeleteArchived = {
            viewModel.deleteArchived(nav)
        }
    }

    ArchivedPreviewScreenContent(viewModel.archived)
}

@Preview(showBackground = true)
@Composable
private fun ArchivedPreviewScreenPreview() {
    val template = genTemplates(1)[0]
    val archived = Draft(template)

    val screen = ArchivedPreviewScreen()
    val nav = rememberNavController()
    TextTemplateTheme {
        AppScaffold(scaffoldOptions = screen.scaffold(nav)) {
            ArchivedPreviewScreenContent(archived)
        }
    }
}
