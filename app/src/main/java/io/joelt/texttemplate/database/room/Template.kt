package io.joelt.texttemplate.database.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.joelt.texttemplate.models.slots.EscapedString
import io.joelt.texttemplate.models.slots.deserializeTemplateBody
import io.joelt.texttemplate.models.slots.serializeTemplateBody
import java.time.LocalDateTime

@Entity
data class Template(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "created_on") val createdOn: LocalDateTime,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "body") val body: String
)

fun ModelTemplate.toRoom(overrideId: Long = this.id): Template =
    Template(overrideId, this.createdOn, this.name, serializeTemplateBody(this.body))

fun Template.toModel(): ModelTemplate =
    ModelTemplate(this.id, this.createdOn, this.name, deserializeTemplateBody(EscapedString(this.body)))
