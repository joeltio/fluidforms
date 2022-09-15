package io.joelt.texttemplate.models.slots

abstract class Slot {
    abstract fun toDisplayString(): String
    abstract fun serializeToString(): String
    abstract fun setValueFromString(s: String)
}