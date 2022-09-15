package io.joelt.texttemplate.models.slots

fun slotPlaceholder(slot: Slot) = when (slot) {
    is PlainTextSlot -> "Text"
    else -> throw NotImplementedError("slot type does not have a placeholder text implemented")
}
