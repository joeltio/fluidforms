package io.joelt.fluidforms.database.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.joelt.fluidforms.models.slots.EscapedString
import io.joelt.fluidforms.models.slots.deserializeFormBody
import io.joelt.fluidforms.models.slots.serializeFormBody
import java.time.LocalDateTime

@Entity
data class Draft(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "created_on") val createdOn: LocalDateTime,
    @ColumnInfo(name = "archived") val archived: Boolean,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "body") val body: String,
    @ColumnInfo(name = "last_edited_index") val lastEditedIndex: Int,
    @ColumnInfo(name = "last_edited_on") val lastEditedOn: LocalDateTime
)

fun ModelDraft.toRoom(overrideId: Long = this.id): Draft =
    Draft(
        overrideId,
        this.createdOn,
        this.archived,
        this.name,
        serializeFormBody(this.body),
        this.lastEditedIndex,
        this.lastEditedOn
    )

fun Draft.toModel(): ModelDraft =
    ModelDraft(
        this.id,
        this.createdOn,
        this.archived,
        this.name,
        deserializeFormBody(EscapedString(this.body)),
        this.lastEditedIndex,
        this.lastEditedOn
    )
