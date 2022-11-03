package io.joelt.texttemplate.database.room

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return value?.let {
            val instant = Instant.ofEpochMilli(it)
            val zonedDt = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
            return zonedDt.toLocalDateTime()
        }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        return date?.let {
            val zonedDt = it.atZone(ZoneId.systemDefault())
            return zonedDt.toInstant().toEpochMilli()
        }
    }
}
