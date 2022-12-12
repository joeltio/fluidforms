package io.joelt.texttemplate.models.slots

import androidx.compose.runtime.Composable

interface SlotInfo {
    val serializeTag: EscapedString
    fun deserialize(
        serializedLabel: EscapedString,
        serializedModifiers: Map<EscapedString, EscapedString>,
        serializedValue: EscapedString
    ): Slot
}

abstract class Slot {
    // Serializing properties
    abstract val info: SlotInfo
    abstract fun serializeModifiers(): Map<EscapedString, EscapedString>
    abstract fun serializeValue(): EscapedString

    // Display properties
    abstract fun displayValue(): String
    abstract val displayLabel: String

    @Composable
    abstract fun displaySlotType(): String
    fun displayDefault(): String {
        val displayVal = displayValue()
        if (displayVal.isEmpty()) {
            return displayLabel
        }
        return displayVal
    }

    // Other properties
    abstract fun makeCopy(displayLabel: String): Slot
}
