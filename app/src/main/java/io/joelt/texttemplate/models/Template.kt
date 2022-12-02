package io.joelt.texttemplate.models

import io.joelt.texttemplate.models.slots.Slot
import java.time.LocalDateTime

data class Template(
    val id: Long = 0,
    val createdOn: LocalDateTime = LocalDateTime.now(),
    val name: String,
    val body: List<Either<String, Slot>>,
) {
    constructor(
        id: Long = 0,
        createdOn: LocalDateTime = LocalDateTime.now(),
        name: String,
        text: String
    ) : this(
        id,
        createdOn,
        name,
        text.toTemplateBody(),
    )

    val text: String by lazy {
        serializeTemplate(body)
    }
}
