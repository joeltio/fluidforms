package io.joelt.texttemplate.database.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class Template(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "created_on") val createdOn: LocalDateTime,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "template") val template: String
)

fun ModelTemplate.toRoom(overrideId: Long = this.id): Template =
    Template(overrideId, this.createdOn, this.name, this.text)

fun Template.toModel(): ModelTemplate =
    ModelTemplate(this.id, this.createdOn, this.name, this.template)
