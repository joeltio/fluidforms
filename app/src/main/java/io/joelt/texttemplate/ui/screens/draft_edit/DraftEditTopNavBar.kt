package io.joelt.texttemplate.ui.screens.draft_edit

import androidx.activity.compose.BackHandler
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import io.joelt.texttemplate.R
import io.joelt.texttemplate.navigation.Route
import io.joelt.texttemplate.ui.screens.drafts.drafts

fun NavHostController.navigateBackToDrafts() {
    navigate(Route.drafts) {
        this@navigateBackToDrafts.graph.startDestinationRoute?.let { route ->
            popUpTo(route)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DraftEditTopNavBar(nav: NavHostController) {
    BackHandler(enabled = true) {
        nav.navigateBackToDrafts()
    }

    val backIconBtn = @Composable {
        IconButton(onClick = {
            nav.navigateBackToDrafts()
        }) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = stringResource(id = R.string.navbar_back),
            )
        }
    }

    TopAppBar(
        title = {},
        navigationIcon = backIconBtn,
    )
}