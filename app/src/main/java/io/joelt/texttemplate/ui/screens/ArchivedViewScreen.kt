package io.joelt.texttemplate.ui.screens

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
import io.joelt.texttemplate.ui.viewmodels.ArchivedViewViewModel
import org.koin.androidx.compose.koinViewModel

fun NavHostController.navigateToArchivedEdit(archivedId: Long) {
    this.navigate("archived/$archivedId/edit")
}

class ArchivedViewScreen : Screen {
    override fun route(): String = "archived/{archivedId}/edit"

    override fun arguments(): List<NamedNavArgument> = listOf(
        navArgument("archivedId") { type = NavType.LongType },
    )

    override fun makeComposable(backStackEntry: NavBackStackEntry): ScreenComposable =
        { nav, scaffold ->
            scaffold.changeNavBars(ScaffoldType.ARCHIVED_VIEW_SCREEN)
            val archivedId = backStackEntry.arguments!!.getLong("archivedId")
            ArchivedEditScreen(nav, scaffold, archivedId)
        }
}

@Composable
private fun ArchivedEditScreen(
    nav: NavHostController,
    scaffold: ScaffoldController,
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
