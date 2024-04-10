package com.example.notespro.Database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.notespro.Model.Note

@Dao
interface NoteDao {

    @Query("Select * from notes_table order by date desc")
    fun getAllNotes() : LiveData<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note : Note)

    @Query("Delete from notes_table where id=:id")
    suspend fun deleteNote(id : Int)

    @Update
    suspend fun updateNote(note : Note)

    @Query("Select * from notes_table where title like :query or noteContent like :query")
    fun searchNote(query : String?) : LiveData<List<Note>>
}