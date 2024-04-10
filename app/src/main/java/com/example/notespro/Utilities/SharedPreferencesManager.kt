package com.example.notespro.Utilities

import android.content.Context
import com.example.notespro.Model.User
import com.google.gson.Gson

object SharedPreferencesManager {
    private const val PREFS_NAME = "MyPrefs"

    private const val USER_KEY = "user"
    private const val APP_KEY = "app"
    private const val NOTE_KEY = "note"
    private const val VERIFIED_KEY = "verify"
    private const val APP_LOCK_ON_OFF = "app_lock_on_off"

    fun saveUser(context: Context, user: User) {
        val gson = Gson()
        val userJson = gson.toJson(user)

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(USER_KEY, userJson)
        editor.apply()
    }

    fun getUser(context: Context): User? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val userJson = prefs.getString(USER_KEY, null)
        return if (userJson != null) {
            val gson = Gson()
            gson.fromJson(userJson, User::class.java)
        } else {
            null
        }
    }

    fun setAppPassword(context : Context, password : String){
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(APP_KEY, password)
        editor.apply()
    }

    fun getAppPassword(context : Context) : String?{
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(APP_KEY, null)
    }

    fun setNotePassword(context : Context, password : String){
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(NOTE_KEY, password)
        editor.apply()
    }

    fun getNotePassword(context : Context) : String?{
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(NOTE_KEY, null)
    }

    fun setVerified(context : Context){
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean(VERIFIED_KEY, true)
        editor.apply()
    }

    fun getVerified(context : Context) : Boolean{
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(VERIFIED_KEY, false)
    }

    fun setAppLockOn(context: Context){
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean(APP_LOCK_ON_OFF, true)
        editor.apply()
    }

    fun setAppLockOff(context: Context){
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean(APP_LOCK_ON_OFF, false)
        editor.apply()
    }

    fun getAppLockOnOff(context: Context) : Boolean{
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(APP_LOCK_ON_OFF, false)
    }
}
