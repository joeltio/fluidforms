package io.joelt.texttemplate.ui.components.slot_editors

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import io.joelt.texttemplate.models.slots.PlainTextSlot
import io.joelt.texttemplate.models.slots.escapeText
import io.joelt.texttemplate.ui.components.PlaceholderTextField

@Composable
fun PlainTextSlotEditor(
    modifier: Modifier = Modifier,
    slot: PlainTextSlot,
    onSlotChange: (slot: PlainTextSlot) -> Unit,
    hasNext: Boolean,
    onNext: () -> Unit,
    onDone: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    PlaceholderTextField(
        modifier = modifier.focusRequester(focusRequester),
        placeholder = slot.displayLabel,
        value = slot.displayValue(),
        onValueChange = { onSlotChange(slot.copy(serializedValue = escapeText(it))) },
        keyboardOptions = KeyboardOptions(
            imeAction = if (hasNext) {
                ImeAction.Next
            } else {
                ImeAction.Done
            }
        ),
        keyboardActions = KeyboardActions(onNext = { onNext() }, onDone = { onDone() })
    )
}
