package io.joelt.texttemplate.models

import io.joelt.texttemplate.models.slots.Slot
import java.time.LocalDateTime

data class Draft(
    val id: Int = 0,
    val name: String,
    val slots: List<Either<String, Slot>>,
    val lastEditedIndex: Int,
    val lastEditedDateTime: LocalDateTime
) {
    constructor(
        id: Int = 0,
        name: String,
        text: String,
        lastEditedIndex: Int,
        lastEditedDateTime: LocalDateTime
    ) : this(id, name, text.toTemplateSlot(), lastEditedIndex, lastEditedDateTime)

    constructor(template: Template, lastEditedIndex: Int, lastEditedDateTime: LocalDateTime) : this(
        0,
        template.name,
        template.text,
        lastEditedIndex,
        lastEditedDateTime
    )

    fun saveCopyNow(
        slots: List<Either<String, Slot>> = this.slots,
        lastEditedIndex: Int = this.lastEditedIndex
    ): Draft {
        return this.copy(
            slots = slots,
            lastEditedIndex = lastEditedIndex,
            lastEditedDateTime = LocalDateTime.now()
        )
    }
}
