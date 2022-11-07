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
import io.joelt.texttemplate.ui.viewmodels.DraftEditViewModel
import org.koin.androidx.compose.koinViewModel

fun NavHostController.navigateToDraftEdit(draftId: Long) {
    this.navigate("drafts/$draftId/edit/0")
}

fun NavHostController.navigateToCreateDraft(fromTemplateId: Long) {
    this.navigate("drafts/0/edit/$fromTemplateId")
}

class DraftEditScreen : Screen {
    override fun route(): String = "drafts/{draftId}/edit/{templateId}"

    override fun arguments(): List<NamedNavArgument> = listOf(
        navArgument("draftId") { type = NavType.LongType },
        navArgument("templateId") { type = NavType.LongType }
    )

    override fun makeComposable(backStackEntry: NavBackStackEntry): ScreenComposable =
        { nav, scaffold ->
            scaffold.changeNavBars(ScaffoldType.DRAFT_EDIT_SCREEN)
            val draftId = backStackEntry.arguments!!.getLong("draftId")
            val templateId = backStackEntry.arguments!!.getLong("templateId")
            DraftEditScreen(nav, scaffold, draftId, templateId)
        }
}

@Composable
private fun DraftEditScreen(
    nav: NavHostController,
    scaffold: ScaffoldController,
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
