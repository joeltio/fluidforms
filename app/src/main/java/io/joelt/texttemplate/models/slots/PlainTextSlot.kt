package io.joelt.texttemplate.models.slots

class PlainTextSlot(var value: String) : Slot() {
    override fun toDisplayString(): String = value

    override fun serializeToString(): String = value

    override fun setValueFromString(s: String) {
        value = s
    }
}