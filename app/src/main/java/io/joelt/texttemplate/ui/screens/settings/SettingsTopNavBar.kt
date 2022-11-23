package io.joelt.texttemplate.ui.screens.settings

import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import io.joelt.texttemplate.R
import io.joelt.texttemplate.ui.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopNavBar(nav: NavHostController) {
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
        title = {
            Text(
                stringResource(id = R.string.navbar_settings),
                style = Typography.titleLarge,
            )
        },
        navigationIcon = backIconBtn,
    )
}