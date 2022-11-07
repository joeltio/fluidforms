package io.joelt.texttemplate.ui.screens

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.*
import io.joelt.texttemplate.navigation.*

fun NavHostController.navigateToDraftEdit(draftId: Long) {
    this.navigate("drafts/$draftId/edit")
}

class DraftEditScreen : Screen {
    override fun route(): String = "drafts/{draftId}/edit"

    override fun arguments(): List<NamedNavArgument> = listOf(
        navArgument("draftId") { type = NavType.LongType }
    )

    override fun makeComposable(backStackEntry: NavBackStackEntry): ScreenComposable =
        { nav, scaffold ->
            scaffold.changeNavBars(ScaffoldType.DRAFT_EDIT_SCREEN)
            val draftId = backStackEntry.arguments!!.getLong("draftId")
            DraftEditScreen(nav, scaffold, draftId)
        }
}

@Composable
private fun DraftEditScreen(nav: NavHostController, scaffold: ScaffoldController, draftId: Long) {
    Text(text = "draft edit screen for draft with id $draftId")
}
