package io.joelt.texttemplate.ui.components

import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import io.joelt.texttemplate.R
import io.joelt.texttemplate.ui.theme.Typography

@Composable
fun TemplateEditTopNavBar(nav: NavHostController) {
    val bgColor = MaterialTheme.colorScheme.tertiaryContainer
    val contentColor = MaterialTheme.colorScheme.onTertiaryContainer

    val backIconBtn = @Composable {
        IconButton(onClick = {
            nav.popBackStack()
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