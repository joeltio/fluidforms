package io.joelt.texttemplate.database.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface DraftDao {
    @Insert
    fun insert(draft: Draft): Long

    @Query("SELECT * FROM draft WHERE archived = :archived")
    fun getDrafts(archived: Boolean): List<Draft>

    @Query("SELECT * FROM draft")
    fun getDrafts(): List<Draft>

    @Query("SELECT * FROM draft WHERE id = :id")
    fun getDraft(id: Long): Draft

    @Update
    fun update(draft: Draft)

    @Query("UPDATE draft SET archived = 1 WHERE id = :id")
    fun updateToArchived(id: Long)

    @Query("DELETE FROM draft WHERE id = :id")
    fun delete(id: Long)
}