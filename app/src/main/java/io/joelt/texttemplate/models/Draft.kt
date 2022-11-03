package io.joelt.texttemplate.models

import io.joelt.texttemplate.models.slots.Slot
import java.time.LocalDateTime

data class Draft(
    val id: Long = 0,
    val createdOn: LocalDateTime = LocalDateTime.now(),
    val archived: Boolean = false,
    val name: String,
    val slots: List<Either<String, Slot>>,
    val lastEditedIndex: Int,
    val lastEditedOn: LocalDateTime
) {
    constructor(
        id: Long = 0,
        createdOn: LocalDateTime = LocalDateTime.now(),
        archived: Boolean = false,
        name: String,
        text: String,
        lastEditedIndex: Int,
        lastEditedOn: LocalDateTime
    ) : this(id, createdOn, archived, name, text.toTemplateSlot(), lastEditedIndex, lastEditedOn)

    constructor(template: Template) : this(
        0,
        LocalDateTime.now(),
        false,
        template.name,
        template.text,
        0,
        LocalDateTime.now()
    )

    val text: String by lazy {
        serializeTemplate(slots)
    }
}
