package io.joelt.texttemplate.ui.screens.archived_preview

import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.joelt.texttemplate.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchivedPreviewTopNavBar(onBack: () -> Unit, onDeleteArchived: () -> Unit) {
    val backIconBtn = @Composable {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = stringResource(id = R.string.all_nav_back),
            )
        }
    }
    TopAppBar(
        title = {},
        navigationIcon = backIconBtn,
        actions = {
            IconButton(onClick = onDeleteArchived) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = stringResource(R.string.archived_preview_delete_archived))
            }
        }
    )
}
