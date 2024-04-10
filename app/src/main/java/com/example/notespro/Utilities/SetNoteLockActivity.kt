package com.example.notespro.Utilities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.notespro.R
import com.example.notespro.databinding.ActivitySetAppLockBinding
import com.example.notespro.databinding.ActivitySetNoteLockBinding

class SetNoteLockActivity : AppCompatActivity() {

    private lateinit var bind : ActivitySetNoteLockBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        StatusBarManager.setStatusBarColorAndContentsColor(this, R.color.dialog_bg_color)

        bind = ActivitySetNoteLockBinding.inflate(layoutInflater)
        setContentView(bind.root)

        setupListeners()
    }

    private fun setupListeners(){
        bind.btnDone.setOnClickListener {

            if(bind.etPassword.text.length < 8) {
                showToast("Password should contain atleast 8 characters")
            }
            else if(bind.etPassword.text.toString() != bind.etConfirmPassword.text.toString()) {
                showToast("Passwords don't match")
            }
            else{
                savePassword()
                finish()
            }
        }

        bind.btnBack.setOnClickListener{
            finish()
        }
    }

    private fun savePassword(){
        SharedPreferencesManager.setNotePassword(this, bind.etPassword.text.toString())
    }

    private fun showToast(message : String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}