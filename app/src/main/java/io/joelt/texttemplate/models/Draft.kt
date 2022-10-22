package io.joelt.texttemplate.models

import java.time.LocalDateTime

data class Draft(val template: Template, val lastEditedIndex: Int, val lastEditedDateTime: LocalDateTime) {
    fun saveCopyNow(
        template: Template = this.template,
        lastEditedIndex: Int = this.lastEditedIndex
    ): Draft {
        return this.copy(
            template = template,
            lastEditedIndex = lastEditedIndex,
            lastEditedDateTime = LocalDateTime.now()
        )
    }
}
