package io.joelt.texttemplate.models.slots

import androidx.compose.runtime.Composable

abstract class Slot {
    var label = ""
    protected abstract fun valueToDisplayString(): String
    // Returns the placeholder if valueToDisplayString is empty
    fun toDisplayString(): String {
        val displayStr = valueToDisplayString()
        if (displayStr == "") {
            return label
        }
        return displayStr
    }

    @Composable
    abstract fun typeName(): String
    abstract fun serializeValue(): String
    abstract fun loadSerializedValue(s: String)
    abstract fun makeCopy(label: String): Slot
}