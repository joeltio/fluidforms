package io.joelt.texttemplate.models.slots

const val END_TAG = "end"
private const val PLAIN_TEXT_TAG = "text"
private const val LABEL_MODIFIER = "label"

/**
 * Parses a string to get the type and modifier.
 *
 * The string should be in a format similar to:
 * `"slotType | mod1Name = \"mod1Value\" | mod2Name = \"mod2Value\""`
 *
 * @param slotTypeAndModifiers the string to parse
 * @return a pair of (slotType, modifierMap), where the modifier map maps from
 * the modifier name to its value i.e. `map[modName] = modValue`
 */
private fun getTypeAndModifiers(
    slotTypeAndModifiers: String
): Pair<String, Map<String, String>> {
    val split = slotTypeAndModifiers.split("|")
    val slotType = split.first().trim()
    val modifiers = mutableMapOf<String, String>()
    split.forEachIndexed { index, s ->
        if (index == 0) {
            return@forEachIndexed
        }

        // modName = "modValue"
        val (modName, modValue) = s.split("=").map { it.trim() }
        modifiers[modName] = modValue.substring(1, modValue.lastIndex)
    }

    return Pair(slotType, modifiers)
}

fun createSlot(slotTypeAndModifiers: String, slotBody: String): Slot {
    val (slotType, modifiers) = getTypeAndModifiers(slotTypeAndModifiers)

    when (slotType) {
        PLAIN_TEXT_TAG -> {
            return PlainTextSlot(slotBody).apply {
                label = modifiers[LABEL_MODIFIER] ?: ""
            }
        }
        else -> throw Exception("slot type not recognised")
    }
}

private fun createTagString(slot: Slot): String {
    var tag = when (slot) {
        is PlainTextSlot -> PLAIN_TEXT_TAG
        else -> throw Exception("slot type not recognised")
    }

    if (slot.label.isNotEmpty()) {
        tag += " | $LABEL_MODIFIER = \"${slot.label}\""
    }

    return tag
}

fun createSlotString(slot: Slot): String {
    val tag = createTagString(slot)
    return "{% $tag %}${slot.serializeValue()}{% $END_TAG %}"
}
