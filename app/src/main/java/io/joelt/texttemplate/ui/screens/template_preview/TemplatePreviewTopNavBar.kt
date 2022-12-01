package io.joelt.texttemplate.ui.screens.template_preview

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
import androidx.navigation.NavHostController
import io.joelt.texttemplate.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplatePreviewTopNavBar(
    onBack: () -> Unit,
    onDeleteTemplate: () -> Unit,
    onEditTemplate: () -> Unit
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
                    contentDescription = stringResource(id = R.string.navbar_back),
                )
            }
        },
        actions = {
            IconButton(onClick = onDeleteTemplate) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete_template)
                )
            }
            IconButton(onClick = {
                showMenu = true
            }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.menu_show_menu),
                )
            }
            DropdownMenu(expanded = showMenu, onDismissRequest = {
                showMenu = false
            }) {
                DropdownMenuItem(text = {
                    Text(text = stringResource(R.string.menu_edit_template))
                }, onClick = onEditTemplate, leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = stringResource(R.string.menu_edit_template)
                    )
                })
            }
        },
    )
}