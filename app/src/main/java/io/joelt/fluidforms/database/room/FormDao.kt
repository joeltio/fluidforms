package io.joelt.fluidforms.database.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface FormDao {
    @Insert
    fun insert(form: Form): Long

    @Query("SELECT * FROM form ORDER BY form.created_on DESC")
    fun getAll(): List<Form>

    @Query("SELECT * FROM form WHERE id = :id")
    fun get(id: Long): Form

    @Update
    fun update(form: Form)

    @Query("DELETE FROM form WHERE id = :id")
    fun delete(id: Long)
}
