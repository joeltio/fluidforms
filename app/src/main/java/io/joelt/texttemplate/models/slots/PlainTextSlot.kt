package io.joelt.texttemplate.models.slots

class PlainTextSlot(var value: String) : Slot() {
    override val placeholderStr = "Text"
    override fun valueToDisplayString(): String = value
    override fun serializeValue() = value
    override fun loadSerializedValue(s: String) {
        value = s
    }
}