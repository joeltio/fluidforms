package io.joelt.texttemplate.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.joelt.texttemplate.R
import io.joelt.texttemplate.models.*
import io.joelt.texttemplate.models.slots.Slot
import io.joelt.texttemplate.ui.components.slot_editors.SlotEditor

enum class EditorMode {
    NAVIGATION,
    EDIT
}

data class DraftEditorState(
    val name: String,
    val body: List<Either<String, Slot>>,
) {
    constructor(draft: Draft) : this(draft.name, draft.body)
}

@Composable
fun DraftEditor(
    state: DraftEditorState,
    onStateChange: (newState: DraftEditorState) -> Unit
) {
    var selectedIndex by remember {
        mutableStateOf(state.body.indexOfFirst { it is Either.Right })
    }
    var mode by remember {
        mutableStateOf(EditorMode.NAVIGATION)
    }

    val viewLayoutState = rememberTemplateViewLayoutState()
    TemplateViewLayout(
        state = viewLayoutState,
        name = {
            PlaceholderTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.name,
                textStyle = LocalTextStyle.current,
                singleLine = true,
                placeholder = stringResource(R.string.template_name_placeholder),
                onValueChange = {
                    onStateChange(state.copy(name = it))
                })
        }, bottomBar = {
            val nextIndex = state.body.nextSlot(selectedIndex)
            val prevIndex = state.body.prevSlot(selectedIndex)

            BottomAppBar(
                actions = {
                    // Previous Button
                    IconButton(enabled = prevIndex != -1, onClick = {
                        selectedIndex = prevIndex
                    }) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.previous_slot)
                        )
                    }

                    // Slot Editor
                    AnimatedVisibility(
                        modifier = Modifier.weight(1f),
                        visible = mode == EditorMode.EDIT
                    ) {
                        SlotEditor(
                            modifier = Modifier.weight(1f),
                            slot = (state.body[selectedIndex] as Either.Right).value,
                            onSlotChanged = {
                                val newSlots = state.body.toMutableList()
                                newSlots.removeAt(selectedIndex)
                                newSlots.add(selectedIndex, Either.Right(it))
                                onStateChange(state.copy(body = newSlots))
                            })
                    }

                    // Next Button
                    IconButton(enabled = nextIndex != -1, onClick = {
                        selectedIndex = nextIndex
                    }) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = stringResource(R.string.next_slot)
                        )
                    }
                },
                floatingActionButton = if (mode == EditorMode.EDIT || selectedIndex == -1) {
                    null
                } else {
                    {
                        FloatingActionButton(onClick = {
                            mode = EditorMode.EDIT
                        }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = stringResource(
                                    R.string.edit_slot
                                )
                            )
                        }
                    }
                }
            )
        }) {
        TemplateBodyPreview(
            body = state.body,
            selectedSlotIndex = if (selectedIndex == -1) {
                null
            } else {
                selectedIndex
            },
            onSlotClick = { selectedIndex = it },
            onRequestScroll = { offset, height ->
                viewLayoutState.scrollBodyToTopOfBottomBar(offset, height)
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DraftEditorPreview() {
    val template = genTemplates(1)[0]
    var state by remember {
        mutableStateOf(DraftEditorState(template.name, template.body))
    }

    Column {
        DraftEditor(state, onStateChange = {
            state = it
        })
    }
}