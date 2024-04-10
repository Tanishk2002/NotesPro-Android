package com.example.notespro.Utilities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.notespro.MainActivity
import com.example.notespro.R
import com.example.notespro.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var bind: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        StatusBarManager.setStatusBarColorAndContentsColor(this, R.color.dialog_bg_color)

        bind = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(bind.root)

        setSwitch()
        setupListeners()
    }

    private fun setSwitch(){
        if(SharedPreferencesManager.getAppLockOnOff(this))
            bind.btnSwitch.isChecked = true
    }

    private fun setupListeners() {
        bind.btnSwitch.setOnCheckedChangeListener { buttonView, isChecked ->

            if(isChecked){
                if(SharedPreferencesManager.getAppPassword(this) == null)
                    bind.btnApp.performClick()
                else
                    SharedPreferencesManager.setAppLockOn(this)
            }
            else
                SharedPreferencesManager.setAppLockOff(this)
        }

        bind.btnApp.setOnClickListener {
            //startActivity(Intent(this, SetAppLockActivity::class.java))
            val intent = Intent(this, EmailVerificationActivity::class.java).apply {
                putExtra(MyConstants.ACTIVITY_KEY, SetAppLockActivity::class.java)
            }
            startActivity(intent)
        }

        bind.btnNote.setOnClickListener {
            //startActivity(Intent(this, SetNoteLockActivity::class.java))
            val intent = Intent(this, EmailVerificationActivity::class.java).apply {
                putExtra(MyConstants.ACTIVITY_KEY, SetNoteLockActivity::class.java)
            }
            startActivity(intent)
        }

        bind.btnDetails.setOnClickListener {
            startActivity(Intent(this, EditDetailsActivity::class.java))
        }

        bind.btnBack.setOnClickListener{
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        bind.btnSwitch.isChecked =
            SharedPreferencesManager.getAppPassword(this) != null && SharedPreferencesManager.getAppLockOnOff(
                this
            )
    }
}