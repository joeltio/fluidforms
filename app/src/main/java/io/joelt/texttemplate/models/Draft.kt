package io.joelt.texttemplate.models

import io.joelt.texttemplate.models.slots.Slot
import java.time.LocalDateTime

data class Draft(
    val id: Long = 0,
    val createdOn: LocalDateTime = LocalDateTime.now(),
    val archived: Boolean = false,
    val name: String,
    val body: List<Either<String, Slot>>,
    val lastEditedIndex: Int,
    val lastEditedOn: LocalDateTime
) {
    companion object {
        fun fromTemplate(template: Template) = Draft(
            0,
            LocalDateTime.now(),
            false,
            template.name,
            template.body,
            0,
            LocalDateTime.now()
        )
    }
}
