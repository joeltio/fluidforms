package io.joelt.texttemplate.models.slots

abstract class Slot {
    protected abstract val placeholderStr: String
    protected abstract fun valueToDisplayString(): String
    // Returns the placeholder if valueToDisplayString is empty
    fun toDisplayString(): String {
        val displayStr = valueToDisplayString()
        if (displayStr == "") {
            return placeholderStr
        }
        return displayStr
    }

    abstract fun serializeValue(): String
    abstract fun loadSerializedValue(s: String)
}