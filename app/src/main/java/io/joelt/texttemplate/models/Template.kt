package io.joelt.texttemplate.models

import io.joelt.texttemplate.models.slots.Slot

data class Template(val name: String, val slots: List<Either<String, Slot>>) {
    constructor(name: String, text: String): this(name, text.toTemplateSlot())

    val text: String
        get() {
            return serializeTemplate(slots)
        }
}
