package com.example.notespro.Model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.notespro.Repository.NoteRepository

class NoteViewModelFactory(val application: Application, private val noteRepository: NoteRepository) : ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NoteViewModel(application, noteRepository) as T
    }

}