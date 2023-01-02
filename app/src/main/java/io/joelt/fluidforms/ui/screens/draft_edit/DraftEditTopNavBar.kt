package io.joelt.fluidforms.ui.screens.draft_edit

import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import io.joelt.fluidforms.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DraftEditTopNavBar(onBack: () -> Unit, onSave: () -> Unit, onDelete: () -> Unit, onCopyToClipboard: () -> Unit) {
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
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.draft_edit_delete_draft)
                )
            }
            IconButton(onClick = onCopyToClipboard) {
                Icon(
                    painter = painterResource(R.drawable.ic_baseline_content_copy_24),
                    contentDescription = stringResource(R.string.draft_edit_copy_to_clipboard)
                )
            }
            IconButton(onClick = onSave) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = stringResource(R.string.draft_edit_save_draft)
                )
            }
        }
    )
}
