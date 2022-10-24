package io.joelt.texttemplate.ui.components

import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import io.joelt.texttemplate.R
import io.joelt.texttemplate.ui.theme.Typography
import io.joelt.texttemplate.ui.theme.onTertiarySecondary

@Composable
fun TopNavBar(navController: NavController) {
    val bgColor = MaterialTheme.colorScheme.tertiaryContainer
    val contentColor = MaterialTheme.colorScheme.onTertiarySecondary

    val settingsIconBtn = @Composable {
        IconButton(onClick = {
            navController.navigate("settings")
        }) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = stringResource(id = R.string.navbar_settings),
                tint = contentColor
            )
        }
    }
    TopAppBar(
        title = {
            Text(
                stringResource(id = R.string.app_name),
                color = contentColor,
                style = Typography.titleLarge,
            )
        },
        navigationIcon = settingsIconBtn,
        backgroundColor = bgColor,
        contentColor = contentColor
    )
}
