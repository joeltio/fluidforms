package io.joelt.texttemplate.database.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Template::class, Draft::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun draftDao() : DraftDao
    abstract fun templateDao() : TemplateDao
}
