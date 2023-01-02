package io.joelt.fluidforms.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import io.joelt.fluidforms.R
import io.joelt.fluidforms.models.*
import io.joelt.fluidforms.models.slots.Slot
import io.joelt.fluidforms.ui.components.slot_editors.SlotEditor

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
    var scrollOffsets by remember { mutableStateOf<Map<Int, Float>>(emptyMap()) }
    var mode by remember { mutableStateOf(EditorMode.NAVIGATION) }
    val viewLayoutState = rememberFormViewLayoutState()

    // TODO: Constantly updating on every scroll is not optimal, but currently there is no way to
    //  track when the ime animation finishes. This feature is introduced only in compose-foundation
    //  1.4.0-alpha01 as WindowInsets.imeAnimationTarget
    LaunchedEffect(selectedIndex, WindowInsets.ime.getBottom(LocalDensity.current)) {
        if (selectedIndex == -1) {
            return@LaunchedEffect
        }
        viewLayoutState.scrollBodyToTopOfBottomBar(scrollOffsets[selectedIndex]!!)
    }

    FormViewLayout(
        state = viewLayoutState,
        name = {
            PlaceholderTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.name,
                textStyle = LocalTextStyle.current,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = {
                    if (mode == EditorMode.NAVIGATION && selectedIndex != -1) {
                        mode = EditorMode.EDIT
                    }
                    return@KeyboardActions this.defaultKeyboardAction(ImeAction.Next)
                }),
                singleLine = true,
                placeholder = stringResource(R.string.editor_name_placeholder),
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
                            contentDescription = stringResource(R.string.draft_editor_previous_slot)
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
                            },
                            hasNext = nextIndex != -1,
                            onNext = {
                                selectedIndex = nextIndex
                            },
                            onDone = {
                                mode = EditorMode.NAVIGATION
                            })
                    }

                    // Next Button
                    IconButton(enabled = nextIndex != -1, onClick = {
                        selectedIndex = nextIndex
                    }) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = stringResource(R.string.draft_editor_next_slot)
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
                                    R.string.draft_editor_edit_slot
                                )
                            )
                        }
                    }
                }
            )
        }) {
        FormBodyPreview(
            body = state.body,
            selectedSlotIndex = if (selectedIndex == -1) {
                null
            } else {
                selectedIndex
            },
            onSlotClick = { selectedIndex = it },
            onScrollOffsetCalculated = { scrollOffsets = it },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DraftEditorPreview() {
    val form = genForms(1)[0]
    var state by remember {
        mutableStateOf(DraftEditorState(form.name, form.body))
    }

    Column {
        DraftEditor(state, onStateChange = {
            state = it
        })
    }
}