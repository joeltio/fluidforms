package io.joelt.texttemplate.database.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TemplateDao {
    @Insert
    fun insert(template: Template): Long

    @Query("SELECT * FROM template ORDER BY template.created_on DESC")
    fun getAll(): List<Template>

    @Query("SELECT * FROM template WHERE id = :id")
    fun get(id: Long): Template

    @Update
    fun update(template: Template)

    @Query("DELETE FROM template WHERE id = :id")
    fun delete(id: Long)
}
