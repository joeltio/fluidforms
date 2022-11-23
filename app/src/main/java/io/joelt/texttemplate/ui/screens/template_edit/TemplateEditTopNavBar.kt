package io.joelt.texttemplate.ui.screens.template_edit

import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import io.joelt.texttemplate.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateEditTopNavBar(nav: NavHostController, onSave: () -> Unit) {
    val backIconBtn = @Composable {
        IconButton(onClick = {
            nav.popBackStack()
        }) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(id = R.string.navbar_back),
            )
        }
    }
    TopAppBar(
        title = {},
        navigationIcon = backIconBtn,
        actions = {
            IconButton(onClick = {
                onSave()
            }) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = stringResource(R.string.save_template),
                )
            }
        },
    )
}