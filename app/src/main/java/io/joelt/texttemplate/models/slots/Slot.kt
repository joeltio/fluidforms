package io.joelt.texttemplate.models.slots

abstract class Slot {
    abstract fun valueToString(): String
    abstract fun setValueFromString(s: String)
}