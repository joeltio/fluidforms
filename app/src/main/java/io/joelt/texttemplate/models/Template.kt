package io.joelt.texttemplate.models

import io.joelt.texttemplate.models.slots.Slot

data class Template(val id: Int = 0, val name: String, val slots: List<Either<String, Slot>>) {
    constructor(id: Int = 0, name: String, text: String): this(id, name, text.toTemplateSlot())

    val text: String
        get() {
            return serializeTemplate(slots)
        }
}

fun List<Either<String, Slot>>.nextSlot(from: Int): Int {
    if (from >= this.lastIndex) {
        return -1
    }

    this.subList(from + 1, this.size).forEachIndexed { index, either ->
        if (either is Either.Right) {
            return from + 1 + index
        }
    }
    return -1
}

fun List<Either<String, Slot>>.prevSlot(from: Int): Int {
    if (from <= 0) {
        return -1
    }

    this.subList(0, from).asReversed().forEachIndexed { index, either ->
        if (either is Either.Right) {
            return from - index - 1
        }
    }
    return -1
}
