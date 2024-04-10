package com.example.notespro.Utilities

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.notespro.Model.User
import com.example.notespro.R
import com.example.notespro.UserDetailActivity
import com.example.notespro.databinding.ActivityEmailVerificationBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

class EmailVerificationActivity : AppCompatActivity() {

    private lateinit var bind: ActivityEmailVerificationBinding
    private var activityToStart: Class<*>? = null
    private var verficationCode: Int? = null
    private lateinit var countDownTimer: CountDownTimer
    private var userDetailsArr: Array<String>? = null
    private val disabledColor = "#858585"
    private val enabledColor = "#A674FF"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        StatusBarManager.setStatusBarColorAndContentsColor(this, R.color.dialog_bg_color)

        bind = ActivityEmailVerificationBinding.inflate(layoutInflater)
        setContentView(bind.root)

        userDetailsArr = intent.getStringArrayExtra(MyConstants.USER_KEY) as Array<String>
        activityToStart = intent.getSerializableExtra(MyConstants.ACTIVITY_KEY) as Class<*>

        sendMail()

        initResendButton()
        setUpConfirmButtonListener()
        setUpEditTexts()
        startTimer()

        bind.et1.clearFocus()
    }

    private fun setVerificationCodeForCurrentSession() {
        verficationCode = Random.nextInt(1000, 10000)
    }

    private fun getRecepientEmail() : String{
        if(userDetailsArr != null)
            return userDetailsArr!![3]
        else
            return SharedPreferencesManager.getUser(this)!!.email
    }

    private fun sendMail() {
        setVerificationCodeForCurrentSession()
        var recepientEmail = getRecepientEmail()

        Log.d("are", "email : $recepientEmail")

        CoroutineScope(Dispatchers.IO).launch {
            val sendMail = SendMail()
            sendMail.send(
                verficationCode!!,
                recepientEmail,
                object : SendMail.SendMailCallback {
                    override fun onSuccess() {
                        CoroutineScope(Dispatchers.Main).launch {
                            showToast("Email sent")
                        }
                    }

                    override fun onError() {
                        CoroutineScope(Dispatchers.Main).launch {
                            showToast("Error sending email")
                        }
                    }
                })
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun initResendButton() {
        bind.btnResend.isClickable = false;
        bind.btnResend.setTextColor(Color.parseColor(disabledColor))
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                bind.timer.text = "$secondsRemaining"
            }

            override fun onFinish() {
                // When the timer finishes, make the button clickable
                bind.btnResend.isClickable = true
                bind.btnResend.setTextColor(Color.parseColor(enabledColor))
                bind.btnResend.setOnClickListener {
                    // Restart the timer
                    sendMail()
                    bind.btnResend.isClickable = false
                    bind.btnResend.setTextColor(Color.parseColor(disabledColor))
                    startTimer()
                }
                bind.timer.text = ""
            }
        }
        countDownTimer.start()
    }

    private fun setUpConfirmButtonListener() {
        bind.btnConfirm.setOnClickListener {
            var enteredCode = "${bind.et1.text}${bind.et2.text}${bind.et3.text}${bind.et4.text}"
            if (enteredCode == verficationCode.toString()) {
                if (userDetailsArr != null)
                    setVerificationStatusAndSaveUser()
                startActivity(Intent(this, activityToStart))
                finish()
            }
        }
    }

    private fun setVerificationStatusAndSaveUser() {
        SharedPreferencesManager.setVerified(this)
        var user =
            User(userDetailsArr!![0], userDetailsArr!![1], userDetailsArr!![2], userDetailsArr!![3])
        SharedPreferencesManager.saveUser(this, user)
    }

    private fun setUpEditTexts() {
        //set text change listeners
        bind.et1.addTextChangedListener(DigitTextWatcher(bind.et1, bind.et2))
        bind.et2.addTextChangedListener(DigitTextWatcher(bind.et2, bind.et3))
        bind.et3.addTextChangedListener(DigitTextWatcher(bind.et3, bind.et4))
        bind.et4.addTextChangedListener(
            DigitTextWatcher(
                bind.et4,
                null
            )
        ) // Last EditText, no next EditText

        // Set key listeners
        bind.et2.setOnKeyListener(BackspaceListener(bind.et2, bind.et1))
        bind.et3.setOnKeyListener(BackspaceListener(bind.et3, bind.et2))
        bind.et4.setOnKeyListener(BackspaceListener(bind.et4, bind.et3))
    }

    private inner class DigitTextWatcher(
        private val currentEditText: EditText,
        private val nextEditText: EditText?
    ) :
        TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            if (s != null && s.length == 1 && nextEditText != null) {
                nextEditText.requestFocus()
            }
        }
    }

    private inner class BackspaceListener(
        private val currentEditText: EditText,
        private val previousEditText: EditText
    ) :
        View.OnKeyListener {
        override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
            if (keyCode == KeyEvent.KEYCODE_DEL && event?.action == KeyEvent.ACTION_DOWN && currentEditText.text.isEmpty()) {
                previousEditText.requestFocus()
                return true
            }
            return false
        }
    }
}