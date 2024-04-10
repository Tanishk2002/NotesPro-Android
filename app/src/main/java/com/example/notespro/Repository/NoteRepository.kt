package com.example.notespro.Repository

import androidx.lifecycle.LiveData
import com.example.notespro.Database.NoteDatabase
import com.example.notespro.Model.Note

class NoteRepository(private val db : NoteDatabase) {

    fun getAllNotes() : LiveData<List<Note>>
    {
        return db.getDao().getAllNotes()
    }

    suspend fun insertNote(note : Note)
    {
        db.getDao().insertNote(note)
    }

    suspend fun deleteNote(id : Int)
    {
        db.getDao().deleteNote(id)
    }

    suspend fun updateNote(note : Note)
    {
        db.getDao().updateNote(note)
    }

    fun searchNote(query : String?) : LiveData<List<Note>>
    {
        return db.getDao().searchNote(query)
    }
}