package io.joelt.fluidforms.ui.screens.settings

import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.joelt.fluidforms.R
import io.joelt.fluidforms.ui.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopNavBar(onBack: () -> Unit) {
    val backIconBtn = @Composable {
        IconButton(onBack) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = stringResource(id = R.string.all_nav_back),
            )
        }
    }
    TopAppBar(
        title = {
            Text(
                stringResource(id = R.string.home_nav_settings),
                style = Typography.titleLarge,
            )
        },
        navigationIcon = backIconBtn,
    )
}