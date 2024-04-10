package com.example.notespro.Utilities

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.notespro.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class SendMail {

    interface SendMailCallback {
        fun onSuccess()
        fun onError()
    }

    fun send(verificationCode : Int, stringReceiverEmail : String, callback : SendMailCallback) {

        //Specify these variables according to your choice
        val stringSenderEmail = ""
        val stringPasswordSenderEmail = ""

        val stringHost = "smtp.gmail.com"

        val properties = Properties()

        properties["mail.smtp.host"] = stringHost
        properties["mail.smtp.port"] = "465"
        properties["mail.smtp.ssl.enable"] = "true"
        properties["mail.smtp.auth"] = "true"

        val session = Session.getInstance(properties, object : javax.mail.Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(stringSenderEmail, stringPasswordSenderEmail)
            }
        })

        val mimeMessage = MimeMessage(session)
        mimeMessage.setRecipient(Message.RecipientType.TO, InternetAddress(stringReceiverEmail))

        mimeMessage.subject = "Subject: Verify"
        mimeMessage.setText("$verificationCode is your verification code.")

        try {
            Transport.send(mimeMessage)
            callback.onSuccess()
        } catch (e: MessagingException) {
            callback.onError()
        }
    }

}

