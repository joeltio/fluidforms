package io.joelt.texttemplate.models.slots

const val END_TAG = "end"
private const val LABEL_MODIFIER = "label"
private const val MODIFIER_QUOTE = "\""

val slotTypes: Map<String, SlotInfo> = mapOf(
    plainTextSlotInfo.serializeTag.value to plainTextSlotInfo
)

fun serializeSlot(slot: Slot): String {
    // Create the start tag
    val modifierMap = slot.serializeModifiers().toMutableMap()
    if (slot.displayLabel.isNotEmpty()) {
        modifierMap[EscapedString(LABEL_MODIFIER)] = escapeText(slot.displayLabel)
    }

    val modifierString = modifierMap.map { entry ->
        "${entry.key.value}=$MODIFIER_QUOTE${entry.value.value}$MODIFIER_QUOTE"
    }.joinToString("|")

    var startTag = slot.info.serializeTag.value
    if (modifierString.isNotEmpty()) { startTag += "|$modifierString" }

    return "{%$startTag%}${slot.serializeValue().value}{%$END_TAG%}"
}

fun deserializeSlot(startTag: String, serializedValue: EscapedString): Slot {
    // Separate the tag and the modifiers
    val split = startTag.split("|")
    val tag = split.first().trim()
    val modifierStrings = split.drop(1)
    val modifiers = mutableMapOf(*modifierStrings.map { modifier ->
        val modifierSplit = modifier.split("=")

        // Ensure there are only two items
        if (modifierSplit.size != 2) {
            throw DeserializeException("serialized value's modifiers contain more than one '=' for modifier $modifier")
        }

        var (name, value) = modifierSplit
        name = name.trim()
        value = value.trim()

        // Ensure the modifier value has quotes
        if (value.length < 2
            || !value.startsWith(MODIFIER_QUOTE)
            || !value.endsWith(MODIFIER_QUOTE)
        ) {
            throw DeserializeException("serialized value's modifiers are missing start or end quotes ($MODIFIER_QUOTE) for modifier $modifier")
        }

        value = value.substring(1, value.lastIndex)
        Pair(name, value)
    }.toTypedArray())

    // Process and remove label from the modifier
    val label = modifiers[LABEL_MODIFIER] ?: ""
    modifiers.remove(LABEL_MODIFIER)

    val slotInfo = slotTypes[tag]
        ?: throw DeserializeException("serialized value has unknown tag $tag")

    // Convert all to EscapedString
    return slotInfo.deserialize(
        EscapedString(label),
        mapOf(*modifiers.map { Pair(EscapedString(it.key), EscapedString(it.value)) }
            .toTypedArray()),
        serializedValue
    )
}
