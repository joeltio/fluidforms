package io.joelt.texttemplate.models

interface Slot {
    val placeholderText: String

    fun valueToString(): String
    fun setValueFromString(s: String)
}