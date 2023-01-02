package io.joelt.fluidforms.models

import io.joelt.fluidforms.models.slots.Slot
import java.time.LocalDateTime

data class Form(
    val id: Long = 0,
    val createdOn: LocalDateTime = LocalDateTime.now(),
    val name: String,
    val body: List<Either<String, Slot>>,
)
