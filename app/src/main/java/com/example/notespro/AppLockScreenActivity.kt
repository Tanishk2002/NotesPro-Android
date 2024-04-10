package com.example.notespro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.notespro.Utilities.EmailVerificationActivity
import com.example.notespro.Utilities.MyConstants
import com.example.notespro.Utilities.SetAppLockActivity
import com.example.notespro.Utilities.SharedPreferencesManager
import com.example.notespro.Utilities.StatusBarManager
import com.example.notespro.databinding.ActivityAppLockScreenBinding

class AppLockScreenActivity : AppCompatActivity() {

    private lateinit var bind: ActivityAppLockScreenBinding
    private var writeIndex = 0
    private var delIndex = -1
    private var appPassword : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        StatusBarManager.setStatusBarColorAndContentsColor(this, R.color.lock_screen_top_color)

        bind = ActivityAppLockScreenBinding.inflate(layoutInflater)
        setContentView(bind.root)

        appPassword = SharedPreferencesManager.getAppPassword(this)

        setupListeners()
    }

    private fun incrementIndex() {
        if (writeIndex != 5) {
            ++delIndex
            ++writeIndex
        }

        Log.d("index", "pos : $delIndex $writeIndex")
    }

    private fun decrementIndex() {
        if (delIndex != -1) {
            --delIndex
            --writeIndex
        }

        Log.d("index", "pos : $delIndex $writeIndex")
    }

    private fun setupListeners() {
        bind.btn0.setOnClickListener {
            setEditTextValue(0)
        }

        bind.btn1.setOnClickListener {
            setEditTextValue(1)
        }

        bind.btn2.setOnClickListener {
            setEditTextValue(2)
        }

        bind.btn3.setOnClickListener {
            setEditTextValue(3)
        }

        bind.btn4.setOnClickListener {
            setEditTextValue(4)
        }

        bind.btn5.setOnClickListener {
            setEditTextValue(5)
        }

        bind.btn6.setOnClickListener {
            setEditTextValue(6)
        }

        bind.btn7.setOnClickListener {
            setEditTextValue(7)
        }

        bind.btn8.setOnClickListener {
            setEditTextValue(8)
        }

        bind.btn9.setOnClickListener {
            setEditTextValue(9)
        }

        bind.btnDel.setOnClickListener {
            unsetEdittextValue()
        }

        bind.btnDone.setOnClickListener {
            var currentPassword = "${bind.et0.text}${bind.et1.text}${bind.et2.text}${bind.et3.text}${bind.et4.text}"

            if(currentPassword == appPassword) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            else
                Toast.makeText(this, "Wrong PIN", Toast.LENGTH_SHORT).show()
        }

        bind.btnReset.setOnClickListener {
            val intent = Intent(this, EmailVerificationActivity::class.java).apply {
                putExtra(MyConstants.ACTIVITY_KEY, SetAppLockActivity::class.java)
            }
            startActivity(intent)
        }
    }

    private fun setEditTextValue(value: Int) {

        if (writeIndex != 5) {
            when (writeIndex) {
                0 -> bind.et0.setText(value.toString())
                1 -> bind.et1.setText(value.toString())
                2 -> bind.et2.setText(value.toString())
                3 -> bind.et3.setText(value.toString())
                4 -> bind.et4.setText(value.toString())
            }
        }

        incrementIndex()
    }

    private fun unsetEdittextValue() {

        if (delIndex != -1) {
            when (delIndex) {
                0 -> bind.et0.setText("")
                1 -> bind.et1.setText("")
                2 -> bind.et2.setText("")
                3 -> bind.et3.setText("")
                4 -> bind.et4.setText("")
            }
        }

        decrementIndex()
    }

    override fun onResume() {
        super.onResume()

        appPassword = SharedPreferencesManager.getAppPassword(this)
    }
}