package io.joelt.fluidforms.ui.screens.form_edit

import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.joelt.fluidforms.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormEditTopNavBar(onSave: () -> Unit, onBack: () -> Unit) {
    val backIconBtn = @Composable {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(id = R.string.all_nav_back),
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
                    contentDescription = stringResource(R.string.form_edit_save_form),
                )
            }
        },
    )
}