package io.joelt.texttemplate.models.slots

const val END_TAG = "end"
private const val PLAIN_TEXT_TAG = "text"

fun createSlot(slotType: String, slotBody: String): Slot = when (slotType) {
    PLAIN_TEXT_TAG -> PlainTextSlot(slotBody)
    else -> throw Exception("slot type not recognised")
}

fun createSlotString(slot: Slot): String {
    val tag = when (slot) {
        is PlainTextSlot -> PLAIN_TEXT_TAG
        else -> throw Exception("slot type not recognised")
    }

    return "{% $tag %}${slot.serializeValue()}{% $END_TAG %}"
}
