package io.joelt.texttemplate.models.slots

interface Slot {
    val placeholderText: String

    fun valueToString(): String
    fun setValueFromString(s: String)
}