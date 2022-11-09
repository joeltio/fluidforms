package io.joelt.texttemplate.ui.screens.archived_view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.*
import io.joelt.texttemplate.navigation.*
import io.joelt.texttemplate.ui.components.SlotsPreview
import org.koin.androidx.compose.koinViewModel

fun NavHostController.navigateToArchivedEdit(archivedId: Long) {
    this.navigate("archived/$archivedId/edit")
}

class ArchivedViewScreen : Screen {
    override val route: String = "archived/{archivedId}/edit"
    override val arguments: List<NamedNavArgument> = listOf(
        navArgument("archivedId") { type = NavType.LongType },
    )

    override fun scaffold(nav: NavHostController) = ScaffoldOptions(
        topBar = { ArchivedViewTopNavBar(nav) }
    )

    @Composable
    override fun Composable(backStackEntry: NavBackStackEntry, nav: NavHostController) {
        val archivedId = backStackEntry.arguments!!.getLong("archivedId")
        ArchivedEditScreen(nav, archivedId)
    }
}

@Composable
private fun ArchivedEditScreen(
    nav: NavHostController,
    archivedId: Long,
    viewModel: ArchivedViewViewModel = koinViewModel()
) {
    viewModel.loadArchived(archivedId)
    val archived = viewModel.archived
    if (archived == null) {
        Spacer(Modifier.height(32.dp))
        CircularProgressIndicator()
        return
    }
    Column {
        Text(text = archived.name)
        SlotsPreview(slots = archived.slots)
    }
}
