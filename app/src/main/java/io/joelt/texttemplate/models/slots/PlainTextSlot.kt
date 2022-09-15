package io.joelt.texttemplate.models.slots

class PlainTextSlot(var value: String) : Slot() {
    override fun valueToString(): String = value

    override fun setValueFromString(s: String) {
        value = s
    }
}