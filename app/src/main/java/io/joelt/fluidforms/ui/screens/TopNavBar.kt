package io.joelt.fluidforms.ui.screens

import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import io.joelt.fluidforms.R
import io.joelt.fluidforms.ui.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavBar(nav: NavHostController, scrollBehaviour: TopAppBarScrollBehavior? = null) {
    val settingsIconBtn = @Composable {
        IconButton(onClick = {
            nav.navigate("settings")
        }) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = stringResource(id = R.string.home_nav_settings),
            )
        }
    }
    TopAppBar(
        title = {
            Text(
                stringResource(id = R.string.app_name),
                style = Typography.titleLarge,
            )
        },
        navigationIcon = settingsIconBtn,
        scrollBehavior = scrollBehaviour
    )
}
