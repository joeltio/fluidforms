package io.joelt.texttemplate.ui.components.slot_editors

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.joelt.texttemplate.models.slots.PlainTextSlot
import io.joelt.texttemplate.models.slots.escapeText
import io.joelt.texttemplate.ui.components.PlaceholderTextField

@Composable
fun PlainTextSlotEditor(modifier: Modifier = Modifier, slot: PlainTextSlot, onSlotChange: (slot: PlainTextSlot) -> Unit) {
    PlaceholderTextField(
        modifier = modifier,
        placeholder = slot.displayLabel,
        value = slot.displayValue(),
        onValueChange = { onSlotChange(slot.copy(serializedValue = escapeText(it))) })
}
