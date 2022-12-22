package io.joelt.texttemplate.ui.components.slot_editors

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.joelt.texttemplate.models.slots.PlainTextSlot
import io.joelt.texttemplate.models.slots.Slot

@Composable
fun SlotEditor(
    modifier: Modifier = Modifier,
    slot: Slot,
    onSlotChanged: (Slot) -> Unit,
    hasNext: Boolean,
    onNext: () -> Unit,
    onDone: () -> Unit
) {
    when (slot) {
        is PlainTextSlot -> {
            PlainTextSlotEditor(modifier, slot, onSlotChanged, hasNext, onNext, onDone)
        }
    }
}
