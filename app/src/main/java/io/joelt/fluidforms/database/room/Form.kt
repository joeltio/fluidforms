package io.joelt.fluidforms.database.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.joelt.fluidforms.models.slots.EscapedString
import io.joelt.fluidforms.models.slots.deserializeFormBody
import io.joelt.fluidforms.models.slots.serializeFormBody
import java.time.LocalDateTime

@Entity
data class Form(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "created_on") val createdOn: LocalDateTime,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "body") val body: String
)

fun ModelForm.toRoom(overrideId: Long = this.id): Form =
    Form(overrideId, this.createdOn, this.name, serializeFormBody(this.body))

fun Form.toModel(): ModelForm =
    ModelForm(this.id, this.createdOn, this.name, deserializeFormBody(EscapedString(this.body)))
