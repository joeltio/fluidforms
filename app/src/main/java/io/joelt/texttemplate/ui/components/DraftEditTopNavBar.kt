package io.joelt.texttemplate.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import io.joelt.texttemplate.R

@Composable
fun DraftEditTopNavBar(nav: NavHostController) {
    val bgColor = MaterialTheme.colorScheme.tertiaryContainer
    val contentColor = MaterialTheme.colorScheme.onTertiaryContainer

    BackHandler(enabled = true) {
        nav.navigate("drafts")
    }

    val backIconBtn = @Composable {
        IconButton(onClick = {
            nav.navigate("drafts")
        }) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = stringResource(id = R.string.navbar_back),
                tint = contentColor
            )
        }
    }

    TopAppBar(
        title = {},
        navigationIcon = backIconBtn,
        backgroundColor = bgColor,
        contentColor = contentColor
    )
}