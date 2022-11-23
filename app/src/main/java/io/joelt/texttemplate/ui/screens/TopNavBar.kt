package io.joelt.texttemplate.ui.screens

import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import io.joelt.texttemplate.R
import io.joelt.texttemplate.ui.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavBar(nav: NavHostController, scrollBehaviour: TopAppBarScrollBehavior? = null) {
    val settingsIconBtn = @Composable {
        IconButton(onClick = {
            nav.navigate("settings")
        }) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = stringResource(id = R.string.navbar_settings),
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
