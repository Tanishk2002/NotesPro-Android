package com.example.notespro.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.notespro.Model.Note
import com.example.notespro.Utilities.MyConstants

@Database(entities = [Note::class], version = 1)
abstract class NoteDatabase : RoomDatabase(){

    abstract fun getDao() : NoteDao

    companion object{

        @Volatile
        private var Instance : NoteDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) : NoteDatabase
        {
            return Instance ?: synchronized(LOCK){
                createDatabase(context).also{
                    Instance = it
                }
            }
        }

        private fun createDatabase(context: Context) : NoteDatabase
        {
            return Room.databaseBuilder(
                context.applicationContext,
                NoteDatabase::class.java,
                MyConstants.DB_NAME
            ).build()
        }
    }

}