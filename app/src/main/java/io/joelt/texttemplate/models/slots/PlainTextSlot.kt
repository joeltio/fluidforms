package io.joelt.texttemplate.models.slots

data class PlainTextSlot(var value: String) : Slot() {
    override fun valueToDisplayString(): String = value
    override fun serializeValue() = value
    override fun loadSerializedValue(s: String) {
        value = s
    }

    override fun makeCopy(label: String): Slot {
        return copy().apply { this.label = label }
    }
}