package io.joelt.texttemplate.ui.screens.draft_edit

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

fun NavHostController.navigateToDraftEdit(draftId: Long) {
    this.navigate("drafts/$draftId/edit/0")
}

fun NavHostController.createDraftRoute(fromTemplateId: Long) = "drafts/0/edit/$fromTemplateId"

class DraftEditScreen : Screen {
    override val route: String = "drafts/{draftId}/edit/{templateId}"
    override val arguments: List<NamedNavArgument> = listOf(
        navArgument("draftId") { type = NavType.LongType },
        navArgument("templateId") { type = NavType.LongType }
    )

    @Composable
    override fun scaffold(nav: NavHostController) = ScaffoldOptions(
        topBar = { DraftEditTopNavBar(nav) },
        bottomBar = { DraftEditBottomAppBar(nav) }
    )

    @Composable
    override fun Composable(backStackEntry: NavBackStackEntry, nav: NavHostController) {
        val draftId = backStackEntry.arguments!!.getLong("draftId")
        val templateId = backStackEntry.arguments!!.getLong("templateId")
        DraftEditScreen(nav, draftId, templateId)
    }
}

@Composable
private fun DraftEditScreen(
    nav: NavHostController,
    draftId: Long,
    templateId: Long,
    viewModel: DraftEditViewModel = koinViewModel()
) {
    val draft = viewModel.draft
    if (draft == null) {
        if (draftId != 0L) {
            viewModel.loadDraft(draftId)
        } else if (templateId != 0L) {
            viewModel.createDraft(templateId)
        }

        Spacer(Modifier.height(32.dp))
        CircularProgressIndicator()
        return
    }

    Column {
        Text(text = draft.name)
        SlotsPreview(slots = draft.slots)
    }
}
