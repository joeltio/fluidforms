package io.joelt.texttemplate.models.slots

fun createSlot(slotType: String, slotBody: String): Slot = when (slotType) {
    "text" -> PlainTextSlot(slotBody)
    else -> throw Exception("slot type not recognised")
}
