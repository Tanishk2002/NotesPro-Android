package com.example.notespro.Model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.notespro.Repository.NoteRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class NoteViewModel(application: Application, private val noteRepository: NoteRepository) :
    AndroidViewModel(application) {

    fun addNote(note: Note) = viewModelScope.launch {
        noteRepository.insertNote(note)
    }

    fun deleteNote(id: Int) = viewModelScope.launch {
        noteRepository.deleteNote(id)
    }

    fun updateNote(note: Note) = viewModelScope.launch {
        noteRepository.updateNote(note)
    }

    fun getAllNotes() = noteRepository.getAllNotes()

    fun searchNote(query : String?) = noteRepository.searchNote(query)
}