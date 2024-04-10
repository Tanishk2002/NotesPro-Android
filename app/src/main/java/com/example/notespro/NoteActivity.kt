package com.example.notespro

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.lang.UCharacter.EastAsianWidth
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.notespro.Database.NoteDatabase
import com.example.notespro.Model.Note
import com.example.notespro.Model.NoteViewModel
import com.example.notespro.Model.NoteViewModelFactory
import com.example.notespro.Repository.NoteRepository
import com.example.notespro.Utilities.EmailVerificationActivity
import com.example.notespro.Utilities.MyConstants
import com.example.notespro.Utilities.SetAppLockActivity
import com.example.notespro.Utilities.SetNoteLockActivity
import com.example.notespro.Utilities.SharedPreferencesManager
import com.example.notespro.Utilities.StatusBarManager
import com.example.notespro.databinding.ActivityNoteBinding
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.Exception
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class NoteActivity() : AppCompatActivity(), EasyPermissions.PermissionCallbacks,
    EasyPermissions.RationaleCallbacks {

    private var bind: ActivityNoteBinding? = null
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var bottomSheetNote: BottomSheetNote
    private var receivedNote: Note? = null
    private var selectedImagePath: String? = ""
    private var isLocked : Boolean = false
    private val READ_STORAGE_PERM = 123
    private val WRITE_STORAGE_PERM = 321
    private val REQUEST_IMAGE_CODE = 456

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        StatusBarManager.setStatusBarColorAndContentsColor(this, R.color.dialog_bg_color)

        bind = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(bind!!.root)

        setupNoteViewModel()
        receivedNote = intent.getParcelableExtra<Note>(MyConstants.NOTE)

        //if we put 0 as id when creating Note object, then room recognizes that it has to autogenerate ids
        //if we give provide any other value than 0, it will put that id into db

        //set data to note activity if it is used for editing note
        setImageListener()
        setNoteData()

        setupNoteActivityButtons()
    }

    private fun setNoteData() {

        if (receivedNote != null) {
            bind!!.etTitle.setText(receivedNote!!.title)
            bind!!.etContent.setText(receivedNote!!.noteContent)

            var timeStampFormat = getCurrentDateTime(receivedNote!!.date)

            bind!!.tvDate.setText(timeStampFormat)

            isLocked = receivedNote!!.locked
            if(isLocked)
                bind!!.topLockUnlockIcon.setImageResource(R.drawable.lock_icon)
            else
                bind!!.topLockUnlockIcon.setImageResource(R.drawable.unlock_icon)

            if (receivedNote!!.imagePath != "") {
                selectedImagePath = receivedNote!!.imagePath
                bind!!.imageView.setImageBitmap(BitmapFactory.decodeFile(selectedImagePath))
                bind!!.imageView.visibility = View.VISIBLE
            }

        } else {
            bind!!.etTitle.setText("")
            bind!!.etContent.setText("")
            bind!!.tvDate.setText("Now")
            bind!!.imageView.visibility = View.GONE
        }
    }

    private fun setImageListener() {
        bind!!.imageView.setOnLongClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("Remove Image")
                setMessage("Are you sure, want to remove image?")
                setPositiveButton("OK") { dialog, which ->
                    selectedImagePath = ""
                    bind!!.imageView.visibility = View.GONE
                }
                setNegativeButton("Cancel") { dialog, which ->
                    dialog.dismiss()
                }.create().show()
            }
            true
        }
    }

    private fun setupNoteViewModel() {
        val noteRepository = NoteRepository(NoteDatabase(this))
        val viewModelProviderFactory = NoteViewModelFactory(application, noteRepository)
        noteViewModel = ViewModelProvider(
            this,
            viewModelProviderFactory
        )[NoteViewModel::class.java] // [] here, same as get()
    }

    private fun setupNoteActivityButtons() {
        bind!!.btnBack.setOnClickListener {
            finish()
        }

        bind!!.btnMenu.setOnClickListener {
            bottomSheetNote = BottomSheetNote(isLocked)
            bottomSheetNote.show(supportFragmentManager, "bottomSheetNote")
        }
    }

    //callbacks from bottomSheetNote
    fun onSelectOption(option: Int) {
        when (option) {
            1 -> insertImage()
            2 -> saveNote()
            3 -> deleteNote()
            4 -> lockUnlockNote()
        }
    }

    private fun insertImage() {
        readStorageTask()
    }

    private fun saveNote() {

        var id = if (receivedNote != null) receivedNote!!.id else 0

        var currentDateTime = System.currentTimeMillis()
        var note =
            Note(
                id,
                bind!!.etTitle.text.toString(),
                bind!!.etContent.text.toString(),
                currentDateTime,
                selectedImagePath,
                isLocked
            )

        if (receivedNote != null) {
            noteViewModel.updateNote(note)
        } else {
            noteViewModel.addNote(note)
        }

        finish()
    }

    private fun deleteNote() {
        if (isLocked) {
            showPasswordDialog(true, this)
        } else {
            if (receivedNote != null)
                noteViewModel.deleteNote(receivedNote!!.id)

            finish()
        }
    }

    private fun lockUnlockNote() {
        //when locking
        if(!isLocked){
            //if note password is set
            if(SharedPreferencesManager.getNotePassword(this) != null){
                isLocked = true
                bind!!.topLockUnlockIcon.setImageResource(R.drawable.lock_icon)
                saveNote()
            }
            //if note password is not set
            else{
                isLocked = false
                //startActivity(Intent(this, SetNoteLockActivity::class.java))
                val intent = Intent(this, EmailVerificationActivity::class.java).apply {
                    putExtra(MyConstants.ACTIVITY_KEY, SetNoteLockActivity::class.java)
                }
                startActivity(intent)
            }
        }
        //when unlocking
        else{
            //check password entered by user with note password, if matches unlock the note
            showPasswordDialog(false,this)
        }
    }

    private fun showPasswordDialog(forDelete : Boolean, context: Context) {
        val builder = AlertDialog.Builder(context)

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

                if(forDelete){
                    if (receivedNote != null) {
                        noteViewModel.deleteNote(receivedNote!!.id)
                        finish()
                    }
                }
                bind!!.topLockUnlockIcon.setImageResource(R.drawable.unlock_icon)
                dialog.dismiss()
            } else {
                isLocked = true
                Toast.makeText(context, "Incorrect password", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun getCurrentDateTime(timeMillis : Long): String {
        val instant = Instant.ofEpochMilli(timeMillis)
        val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy HH:mm:ss", Locale.ENGLISH)
        return dateTime.format(formatter)
    }

    override fun onDestroy() {
        super.onDestroy()
        bind = null
    }

    private fun hasReadStoragePermission(): Boolean {
        return EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun readStorageTask() {
        if (hasReadStoragePermission()) {
            pickImageFromGallery()

        } else {
            EasyPermissions.requestPermissions(
                this,
                "This app needs to access your storage",
                READ_STORAGE_PERM,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(this.packageManager) != null)
            startActivityForResult(intent, REQUEST_IMAGE_CODE)
    }

    private fun getPathFromUri(contentUri: Uri): String? {
        var filePath: String? = null
        var cursor = this.contentResolver.query(contentUri, null, null, null, null)
        if (cursor == null)
            filePath = contentUri.path
        else {
            cursor.moveToFirst()
            var index = cursor.getColumnIndex("_data")
            filePath = cursor.getString(index)
            cursor.close()
        }
        return filePath
    }

    private fun compressBitmap(bitmap: Bitmap): ByteArray {
        var compressionQuality = 50
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressionQuality, byteArrayOutputStream)
        var compressedByteArray = byteArrayOutputStream.toByteArray()
        return compressedByteArray
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CODE && resultCode == Activity.RESULT_OK && data != null) {
            var selectedImageUri = data.data
            if (selectedImageUri != null) {

                val imageFile = File(getPathFromUri(selectedImageUri))
                if (imageFile.length() > 2 * 1024 * 1024) {
                    showDialog()
                } else {
                    compressAndLoadImage(selectedImageUri)
                }

            }
        }
    }

    private fun showDialog() {
        AlertDialog.Builder(this)
            .setTitle("Image Size Too Large")
            .setMessage("The selected image size is too large. Please select an image smaller than 2 MB.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    //for inserting image from the activity
    private fun compressAndLoadImage(selectedImageUri: Uri) {

        try {
            var inputStream = this.contentResolver.openInputStream(selectedImageUri)
            var bitmap = BitmapFactory.decodeStream(inputStream)
            //bind!!.imageView.setImageBitmap(bitmap)
            //bind!!.imageView.visibility = View.VISIBLE

            var compressedByteArray = compressBitmap(bitmap)
            Log.d(
                "compressedSize",
                "Compressed image size : ${(compressedByteArray.size.toFloat() / 1024)}"
            )
            bind!!.imageView.setImageBitmap(
                BitmapFactory.decodeByteArray(
                    compressedByteArray,
                    0,
                    compressedByteArray.size
                )
            )
            bind!!.imageView.visibility = View.VISIBLE

            selectedImagePath = getPathFromUri(selectedImageUri)

        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }


    }

    private fun hasWriteStoragePermission(): Boolean {
        return EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        insertImage()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms))
            AppSettingsDialog.Builder(this).build().show()
    }

    override fun onRationaleAccepted(requestCode: Int) {
    }

    override fun onRationaleDenied(requestCode: Int) {
    }
}