package io.joelt.texttemplate.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.joelt.texttemplate.R
import io.joelt.texttemplate.models.Either
import io.joelt.texttemplate.models.Template
import io.joelt.texttemplate.models.genTemplates
import io.joelt.texttemplate.models.slots.EscapedString
import io.joelt.texttemplate.models.slots.PlainTextSlot
import io.joelt.texttemplate.ui.theme.Typography

data class TemplateEditorState(val templateName: String, val editorState: TemplateBodyEditorState) {
    constructor(template: Template) : this(template.name, TemplateBodyEditorState(template.body))

    fun moveCursorToEnd() = this.copy(editorState = editorState.moveCursorToEnd())
}

@Composable
fun TemplateEditor(
    nameModifier: Modifier = Modifier,
    bodyModifier: Modifier = Modifier,
    state: TemplateEditorState,
    onStateChange: (newState: TemplateEditorState) -> Unit
) {
    val editorState = state.editorState
    TemplateViewLayout(name = {
        PlaceholderTextField(
            modifier = nameModifier.fillMaxWidth(),
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
                if (editorState.selectedSlotIndex != null) {
                    val slot =
                        (editorState.templateBody[editorState.selectedSlotIndex] as Either.Right).value
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = slot.displaySlotType(),
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
            floatingActionButton = if (editorState.selectedSlotIndex == null) {
                {
                    val slotLabel = stringResource(R.string.plain_text_slot_placeholder)
                    FloatingActionButton(
                        onClick = {
                            val slot = PlainTextSlot(slotLabel, EscapedString(""))
                            onStateChange(
                                state.copy(
                                    editorState = editorState.insertSlotAtSelection(
                                        slot
                                    )
                                )
                            )
                        },
                        containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(R.string.add_slot))
                    }
                }
            } else {
                null
            }
        )
    }) {
        val annotatedString = editorState.annotatedString
        PlaceholderTextField(
            modifier = bodyModifier.fillMaxSize(),
            textStyle = LocalTextStyle.current,
            visualTransformation = {
                // Apply the styles from the annotatedString through visualTransformation instead
                TransformedText(annotatedString, OffsetMapping.Identity)
            },
            placeholder = stringResource(R.string.template_body_placeholder),
            value = editorState.textFieldValue,
            onValueChange = {
                val newState = editorState.withNewTextFieldValue(it)
                onStateChange(state.copy(editorState = newState))
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
        TemplateEditor(state = state, onStateChange = {
            state = it
        })
    }
}
