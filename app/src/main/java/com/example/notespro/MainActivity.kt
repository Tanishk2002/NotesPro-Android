package com.example.notespro

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.contextaware.withContextAvailable
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notespro.Adpater.NoteAdapter
import com.example.notespro.Database.NoteDatabase
import com.example.notespro.Model.Note
import com.example.notespro.Model.NoteViewModel
import com.example.notespro.Model.NoteViewModelFactory
import com.example.notespro.Repository.NoteRepository
import com.example.notespro.Utilities.EmailVerificationActivity
import com.example.notespro.Utilities.MyConstants
import com.example.notespro.Utilities.SendMail
import com.example.notespro.Utilities.SetAppLockActivity
import com.example.notespro.Utilities.SetNoteLockActivity
import com.example.notespro.Utilities.SettingsActivity
import com.example.notespro.Utilities.SharedPreferencesManager
import com.example.notespro.Utilities.StatusBarManager
import com.example.notespro.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var noteViewModel: NoteViewModel
    private lateinit var noteAdapter: NoteAdapter
    private var bind: ActivityMainBinding? = null
    private var isLocked: Boolean = false
    private var myQuery : String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        StatusBarManager.setStatusBarColorAndContentsColor(this, R.color.dialog_bg_color)

        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind!!.root)

        bind!!.welcomeText.setText("Hi ${SharedPreferencesManager.getUser(this)!!.firstName}")

        setUpViewModel()
        setUpRecyclerView()
        setupButtonListeners()
    }

    private fun setUpViewModel() {
        val noteRepository = NoteRepository(NoteDatabase(this))
        val viewModelProviderFactory = NoteViewModelFactory(application, noteRepository)
        noteViewModel = ViewModelProvider(
            this,
            viewModelProviderFactory
        )[NoteViewModel::class.java] // [] here, same as get()
    }

    private fun setUpRecyclerView() {
        noteAdapter = NoteAdapter(this, supportFragmentManager)
        bind!!.recyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = noteAdapter

            //getAllNotes will typically gets called once during the initialization, then after that LiveData
            //handles any change in the list as room updates the livedata object associated with a query with updated data
            noteViewModel.getAllNotes().observe(this@MainActivity) { noteList ->
                noteAdapter.differ.submitList(noteList)
                Log.d("tagu", "all notes")
            }
        }
    }

    private fun searchNote(query: String?) {

        myQuery = query

        val searchQuery = "%$query%"

        Log.d("tagu", "searchNote method")

        noteViewModel.searchNote(searchQuery).observe(this) { list ->
            list.let {
                if(!myQuery.isNullOrEmpty()) {
                    Log.d("tagu", "query $query")
                    noteAdapter.differ.submitList(it)
                    Log.d("tagu", "searched notes")
                }
            }
        }
        if(myQuery.isNullOrEmpty())
            noteViewModel.getAllNotes().observe(this@MainActivity) { noteList ->
                noteAdapter.differ.submitList(noteList)
                Log.d("tagu", "all notes")
            }
    }

    private fun setupButtonListeners() {
        bind!!.fab.setOnClickListener {
            var note: Note? = null
            val intent = Intent(this, NoteActivity::class.java).apply {
                putExtra(MyConstants.NOTE, note)
            }
            startActivity(intent)
        }

        bind!!.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        bind!!.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d("tagu", "onquerytextsubmit")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d("tagu", "onquerytextchange")

                if (newText != null)
                    searchNote(newText)
                return true
            }

        })
    }

    override fun onResume() {
        super.onResume()
        bind!!.searchBar.clearFocus()
        bind!!.searchBar.setQuery("", false)
        myQuery = null
        bind!!.recyclerView.scrollToPosition(0)
        bind!!.welcomeText.setText("Hi ${SharedPreferencesManager.getUser(this)!!.firstName}")
    }

    override fun onDestroy() {
        super.onDestroy()
        bind = null
    }

    fun onSelectOption(option: Int, selectedNote: Note) {
        when (option) {
            1 -> editNote(selectedNote)
            2 -> deleteNote(selectedNote)
            3 -> lockUnlockNote(selectedNote)
        }
    }

    private fun editNote(selectedNote: Note) {
        val intent = Intent(this, NoteActivity::class.java).apply {
            putExtra(MyConstants.NOTE, selectedNote)
        }
        startActivity(intent)
    }

    private fun deleteNote(selectedNote: Note) {
        if (selectedNote.locked) {
            showPasswordDialog(true, false, selectedNote)
        } else {
            noteViewModel.deleteNote(selectedNote.id)
        }
    }

    private fun saveNote(selectedNote: Note) {
        var note =
            Note(
                selectedNote.id,
                selectedNote.title,
                selectedNote.noteContent,
                selectedNote.date,
                selectedNote.imagePath,
                isLocked
            )
        noteViewModel.updateNote(note)
    }

    private fun lockUnlockNote(selectedNote: Note) {
        //when locking
        if (!selectedNote.locked) {
            //if note password is set
            if (SharedPreferencesManager.getNotePassword(this) != null) {
                isLocked = true
                saveNote(selectedNote)
            }
            //if note password is not set
            else {
                isLocked = false
                //startActivity(Intent(this, SetNoteLockActivity::class.java))
                val intent = Intent(this, EmailVerificationActivity::class.java).apply {
                    putExtra(MyConstants.ACTIVITY_KEY, SetNoteLockActivity::class.java)
                }
                startActivity(intent)
            }
        }
        //when unlocking
        else {
            //check password entered by user with note password, if matches unlock the note
            showPasswordDialog(false, false, selectedNote)
        }
    }

    fun showPasswordDialog(forDelete: Boolean, openActivity: Boolean, currentNote: Note) {
        val builder = AlertDialog.Builder(this)
        isLocked = currentNote.locked

        val view = layoutInflater.inflate(R.layout.note_password_layout, null, false)
        val editTextPassword = view.findViewById<EditText>(R.id.etPassword)
        val btnReset = view.findViewById<TextView>(R.id.btnReset)
        val btnDone = view.findViewById<Button>(R.id.btnDone)

        builder.setView(view)

        var dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        btnReset.setOnClickListener {
            val intent = Intent(this, EmailVerificationActivity::class.java).apply {
                putExtra(MyConstants.ACTIVITY_KEY, SetNoteLockActivity::class.java)
            }
            startActivity(intent)
            dialog.dismiss()
        }

        btnDone.setOnClickListener {
            val enteredPassword = editTextPassword.text.toString()
            val correctPassword = SharedPreferencesManager.getNotePassword(this)

            if (enteredPassword == correctPassword) {
                isLocked = false
                if (openActivity)
                    noteAdapter.goToNoteActivity(currentNote)
                else if (forDelete)
                    noteViewModel.deleteNote(currentNote.id)
                else
                    saveNote(currentNote)
                dialog.dismiss()
            } else {
                isLocked = true
                Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }
}