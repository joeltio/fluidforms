package io.joelt.texttemplate.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.joelt.texttemplate.R
import io.joelt.texttemplate.models.Either
import io.joelt.texttemplate.models.Template
import io.joelt.texttemplate.models.genTemplates
import io.joelt.texttemplate.models.slots.PlainTextSlot
import io.joelt.texttemplate.ui.theme.Typography

data class TemplateEditorState(val templateName: String, val slotsState: SlotsEditorState) {
    constructor(template: Template) : this(template.name, SlotsEditorState(template.slots))
}

@Composable
fun TemplateEditor(
    state: TemplateEditorState,
    onStateChange: (newState: TemplateEditorState) -> Unit
) {
    val slotsState = state.slotsState
    TemplateViewLayout(name = {
        PlaceholderTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.templateName,
            textStyle = LocalTextStyle.current,
            singleLine = true,
            placeholder = stringResource(R.string.template_name_placeholder),
            onValueChange = {
                onStateChange(state.copy(templateName = it))
            })
    }, bottomBar = {
        BottomAppBar(
            actions = {
                if (slotsState.selectedSlotIndex != null) {
                    val slot =
                        (slotsState.slots[slotsState.selectedSlotIndex] as Either.Right).value
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = slot.typeName(),
                        style = Typography.titleMedium,
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(
                        modifier = Modifier
                            .size(width = 1.dp, height = 36.dp)
                            .background(contentColorFor(BottomAppBarDefaults.containerColor))
                    )
                }
            },
            floatingActionButton = if (slotsState.selectedSlotIndex == null) {
                {
                    val slotLabel = stringResource(R.string.plain_text_slot_placeholder)
                    FloatingActionButton(
                        onClick = {
                            val slot = PlainTextSlot("")
                            slot.label = slotLabel
                            onStateChange(
                                state.copy(
                                    slotsState = slotsState.insertSlotAtSelection(
                                        slot
                                    )
                                )
                            )
                        },
                        containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                    }
                }
            } else {
                null
            }
        )
    }) {
        val annotatedString = slotsState.annotatedString
        PlaceholderTextField(
            modifier = Modifier.fillMaxSize(),
            textStyle = LocalTextStyle.current,
            visualTransformation = {
                // Apply the styles from the annotatedString through visualTransformation instead
                TransformedText(annotatedString, OffsetMapping.Identity)
            },
            placeholder = stringResource(R.string.template_body_placeholder),
            value = slotsState.textFieldValue,
            onValueChange = {
                val newState = slotsState.withNewTextFieldValue(it)
                onStateChange(state.copy(slotsState = newState))
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TemplateEditorPreview() {
    val template = genTemplates(1)[0]
    var state by remember {
        mutableStateOf(TemplateEditorState(template))
    }

    Column {
        TemplateEditor(state, onStateChange = {
            state = it
        })
    }
}