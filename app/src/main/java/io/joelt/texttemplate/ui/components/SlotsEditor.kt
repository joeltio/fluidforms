package io.joelt.texttemplate.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.joelt.texttemplate.R
import io.joelt.texttemplate.models.genTemplates
import io.joelt.texttemplate.models.slots.PlainTextSlot
import io.joelt.texttemplate.ui.theme.Typography

@Composable
fun SlotsEditor(
    state: SlotsEditorState,
    modifier: Modifier = Modifier,
    onStateChange: (SlotsEditorState) -> Unit,
) {
    Column(modifier = Modifier.fillMaxHeight()) {
        PlaceholderTextField(
            modifier = modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .weight(1.0f),
            textStyle = Typography.bodyLarge,
            visualTransformation = {
                // Apply the styles from the annotatedString through visualTransformation instead
                TransformedText(state.annotatedString, OffsetMapping.Identity)
            },
            value = state.textFieldValue,
            onValueChange = {
                val newState = state.withNewTextFieldValue(it)
                onStateChange(newState)
            }
        )
        Box(
            modifier = Modifier
                .imePadding()
                .fillMaxWidth()
        ) {
            BottomAppBar {
                Spacer(modifier = Modifier.weight(1f))
                if (state.selectedSlotIndex == null) {
                    val slotLabel = stringResource(R.string.plain_text_slot_placeholder)
                    IconButton(onClick = {
                        val slot = PlainTextSlot("")
                        slot.label = slotLabel
                        onStateChange(state.insertSlotAtSelection(slot))
                    }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun SlotsEditorPreview() {
    val template = genTemplates(1)[0]
    var state by remember {
        mutableStateOf(SlotsEditorState(template.slots))
    }

    Column {
        SlotsEditor(state, onStateChange = {
            state = it
        })
    }
}
