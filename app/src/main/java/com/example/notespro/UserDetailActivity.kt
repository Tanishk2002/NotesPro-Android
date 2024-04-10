package com.example.notespro

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PatternMatcher
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.example.notespro.Model.User
import com.example.notespro.Utilities.EmailVerificationActivity
import com.example.notespro.Utilities.MyConstants
import com.example.notespro.Utilities.SharedPreferencesManager
import com.example.notespro.Utilities.StatusBarManager
import com.example.notespro.databinding.ActivityUserDetailBinding
import java.util.Calendar
import java.util.regex.Pattern

class UserDetailActivity : AppCompatActivity() {

    private lateinit var bind: ActivityUserDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        StatusBarManager.setStatusBarColorAndContentsColor(this, R.color.dialog_bg_color)

        bind = ActivityUserDetailBinding.inflate(layoutInflater)
        setContentView(bind.root)

        setUpListeners()
    }

    private fun setUpListeners() {
        bind.etDob.setOnClickListener {
            var calendar = Calendar.getInstance()

            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this, DatePickerDialog.OnDateSetListener { _, year1, month1, dayOfMonth1 ->
                    val selectedDate = "$dayOfMonth1/${month1 + 1}/$year1"
                    bind.etDob.setText(selectedDate)
                }, year, month, dayOfMonth
            )
            datePickerDialog.show()
        }

        bind.btnNext.setOnClickListener {
            if (!validateName(
                    bind.etFirstName.text.toString(),
                    bind.etLastName.text.toString()
                )
            ) showToast("Enter a valid name")
            else if (!validateEmail(bind.etEmail.text.toString())) showToast("Enter a valid email")
            else if (bind.etDob.text.isEmpty()) showToast("Enter date of birth")
            else {
                var userDetailsArr = getUser()
                val intent = Intent(this, EmailVerificationActivity::class.java).apply {
                    putExtra(MyConstants.ACTIVITY_KEY, MainActivity::class.java)
                    putExtra(MyConstants.USER_KEY, userDetailsArr)
                    finish()
                }
                startActivity(intent)
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun validateName(firstName: String, lastName: String): Boolean {
        return Regex("^(?!\\\\s+\$)[a-zA-Z\\\\s]+\$").matches(firstName)
                && Regex("^(?!\\\\s+\$)[a-zA-Z\\\\s]+\$").matches(lastName)
    }

    private fun validateEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun getUser() : Array<String>{
        return arrayOf(
            bind.etFirstName.text.toString(),
            bind.etLastName.text.toString(),
            bind.etDob.text.toString(),
            bind.etEmail.text.toString().trim()
        )
    }
}