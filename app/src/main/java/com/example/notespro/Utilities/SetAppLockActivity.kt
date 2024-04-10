package com.example.notespro.Utilities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.notespro.R
import com.example.notespro.databinding.ActivitySetAppLockBinding

class SetAppLockActivity : AppCompatActivity() {

    private lateinit var bind : ActivitySetAppLockBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        StatusBarManager.setStatusBarColorAndContentsColor(this, R.color.dialog_bg_color)

        bind = ActivitySetAppLockBinding.inflate(layoutInflater)
        setContentView(bind.root)

        setupListeners()
    }

    private fun setupListeners(){
        bind.btnDone.setOnClickListener {

            if(bind.etPin.text.length != 5) {
                showToast("PIN must contain 5 digits")
            }
            else if(bind.etPin.text.toString() != bind.etConfirmPin.text.toString()) {
                showToast("PINs don't match")
            }
            else{
                savePasswordAndOnAppLock()
                finish()
            }
        }

        bind.btnBack.setOnClickListener{
            finish()
        }
    }

    private fun savePasswordAndOnAppLock(){
        SharedPreferencesManager.setAppPassword(this, bind.etPin.text.toString())
        SharedPreferencesManager.setAppLockOn(this)
    }

    private fun showToast(message : String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}