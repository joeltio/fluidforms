package io.joelt.fluidforms.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.joelt.fluidforms.R
import io.joelt.fluidforms.models.Either
import io.joelt.fluidforms.models.Form
import io.joelt.fluidforms.models.genForms
import io.joelt.fluidforms.models.slots.EscapedString
import io.joelt.fluidforms.models.slots.PlainTextSlot
import io.joelt.fluidforms.ui.theme.Typography

data class FormEditorState(val formName: String, val editorState: FormBodyEditorState) {
    constructor(form: Form) : this(form.name, FormBodyEditorState(form.body))

    fun moveCursorToEnd() = this.copy(editorState = editorState.moveCursorToEnd())
}

@Composable
fun FormEditor(
    nameModifier: Modifier = Modifier,
    bodyModifier: Modifier = Modifier,
    state: FormEditorState,
    onStateChange: (newState: FormEditorState) -> Unit
) {
    val editorState = state.editorState
    FormViewLayout(name = {
        PlaceholderTextField(
            modifier = nameModifier.fillMaxWidth(),
            value = state.formName,
            textStyle = LocalTextStyle.current,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            singleLine = true,
            placeholder = stringResource(R.string.editor_name_placeholder),
            onValueChange = {
                onStateChange(state.copy(formName = it))
            })
    }, bottomBar = {
        BottomAppBar(
            actions = {
                if (editorState.selectedSlotIndex != null) {
                    val slot =
                        (editorState.formBody[editorState.selectedSlotIndex] as Either.Right).value
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
                    val slotLabel = stringResource(R.string.form_editor_plain_text_slot_placeholder)
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
                        Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(R.string.form_editor_add_slot))
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
            placeholder = stringResource(R.string.form_editor_body_placeholder),
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
private fun FormEditorPreview() {
    val form = genForms(1)[0]
    var state by remember {
        mutableStateOf(FormEditorState(form))
    }

    Column {
        FormEditor(state = state, onStateChange = {
            state = it
        })
    }
}
