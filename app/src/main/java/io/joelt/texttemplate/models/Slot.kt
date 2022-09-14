package io.joelt.texttemplate.models

interface Slot<T> {
    val placeholderText: String
    var value: T

    fun valueToString(): String
    fun setValueFromString(s: String)
}