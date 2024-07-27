package io.joelt.fluidforms.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import io.joelt.fluidforms.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListSettingRow(
    title: String,
    subtitle: String? = null,
    options: Array<String>,
    optionValues: Array<String>,
    selectedOption: String,
    onSelectedOptionChange: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    ListItem(
        modifier = Modifier.clickable { showDialog = true },
        headlineContent = { Text(text = title) },
        supportingContent = subtitle?.let { { Text(text = subtitle) } },
    )
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = title) },
            text = {
                Column(modifier = Modifier.selectableGroup()) {
                    options.forEachIndexed { index, optionName ->
                        val optionValue = optionValues[index]
                        val selected = selectedOption == optionValue
                        ListItem(
                            modifier = Modifier
                                .selectable(
                                    selected = selected,
                                    onClick = {
                                        if (!selected) {
                                            onSelectedOptionChange(optionValues[index])
                                        }
                                    },
                                    role = Role.RadioButton
                                ),
                            leadingContent = {
                                RadioButton(selected = selected, onClick = null)
                            },
                            headlineContent = {
                                Text(text = optionName)
                            }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(text = stringResource(R.string.all_dialog_done))
                }
            }
        )
    }
}

@Preview
@Composable
private fun ListSettingRowPreview() {
    var selectedOption by remember { mutableStateOf("System") }
    ListSettingRow(
        title = "Theme color",
        options = arrayOf("System", "Dark", "Light"),
        optionValues = arrayOf("System", "Dark", "Light"),
        selectedOption = selectedOption,
        onSelectedOptionChange = { selectedOption = it })
}
