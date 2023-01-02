package io.joelt.fluidforms.ui.screens.form_preview

import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import io.joelt.fluidforms.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormPreviewTopNavBar(
    onBack: () -> Unit,
    onDeleteForm: () -> Unit,
    onEditForm: () -> Unit
) {
    var showMenu by remember {
        mutableStateOf(false)
    }
    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(id = R.string.all_nav_back),
                )
            }
        },
        actions = {
            IconButton(onClick = onDeleteForm) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.form_preview_delete_form)
                )
            }
            IconButton(onClick = {
                showMenu = true
            }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.form_preview_show_menu),
                )
            }
            DropdownMenu(expanded = showMenu, onDismissRequest = {
                showMenu = false
            }) {
                DropdownMenuItem(text = {
                    Text(text = stringResource(R.string.form_preview_edit_form))
                }, onClick = onEditForm, leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = stringResource(R.string.form_preview_edit_form)
                    )
                })
            }
        },
    )
}