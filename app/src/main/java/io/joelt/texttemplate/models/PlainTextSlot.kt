package io.joelt.texttemplate.models

class PlainTextSlot : Slot<String> {
    override val placeholderText = "Text"
    override var value = ""

    override fun valueToString(): String = value

    override fun setValueFromString(s: String) {
        value = s
    }
}