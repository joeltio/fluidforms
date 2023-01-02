package io.joelt.fluidforms.models

import io.joelt.fluidforms.models.slots.Slot
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
        fun fromForm(form: Form) = Draft(
            0,
            LocalDateTime.now(),
            false,
            form.name,
            form.body,
            0,
            LocalDateTime.now()
        )
    }
}
