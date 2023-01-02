package io.joelt.fluidforms.models

import io.joelt.fluidforms.models.slots.Slot

fun List<Either<String, Slot>>.nextSlot(from: Int): Int {
    if (from >= this.lastIndex) {
        return -1
    }

    this.subList(from + 1, this.size).forEachIndexed { index, item ->
        if (item is Either.Right) {
            return from + 1 + index
        }
    }
    return -1
}

fun List<Either<String, Slot>>.prevSlot(from: Int): Int {
    if (from <= 0) {
        return -1
    }

    this.subList(0, from).asReversed().forEachIndexed { index, item ->
        if (item is Either.Right) {
            return from - index - 1
        }
    }
    return -1
}
