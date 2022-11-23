package io.joelt.texttemplate.ui.screens.archived_view

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchivedViewTopNavBar(nav: NavHostController) {
    val backIconBtn = @Composable {
        IconButton(onClick = {
            nav.popBackStack()
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