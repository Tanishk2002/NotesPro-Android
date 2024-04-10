package com.example.notespro.Adpater

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.ThumbnailUtils
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.notespro.BottomSheetMain
import com.example.notespro.BottomSheetNote
import com.example.notespro.MainActivity
import com.example.notespro.Model.Note
import com.example.notespro.NoteActivity
import com.example.notespro.R
import com.example.notespro.Utilities.EmailVerificationActivity
import com.example.notespro.Utilities.MyConstants
import com.example.notespro.Utilities.SetNoteLockActivity
import com.example.notespro.Utilities.SharedPreferencesManager
import com.example.notespro.databinding.NoteItemBinding
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class NoteAdapter(private val context: Context, private val fragmentManager : FragmentManager) :
    RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    class NoteViewHolder(var itemBind: NoteItemBinding) : RecyclerView.ViewHolder(itemBind.root)

    //diff callback mechanism to efficiently update contents of the list without re-binding all the items
    //when dataset changes
    private val diffCallback = object : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = NoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(view)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        //give all view ids to holder
        var currentNote = differ.currentList[position]

        setAllViews(currentNote, holder)
        setNoteItemListeners(currentNote, holder)
    }

    private fun setAllViews(currentNote : Note, holder : NoteViewHolder){

        holder.itemBind.tvDate.text = getCurrentDateTime(currentNote.date)

        if(currentNote.locked){
            holder.itemBind.tvTitle.visibility = View.GONE
            holder.itemBind.tvNote.visibility = View.GONE
            holder.itemBind.imageView.visibility = View.GONE

            holder.itemBind.lockIcon.visibility = View.VISIBLE
        }
        else {
            holder.itemBind.lockIcon.visibility = View.GONE
            holder.itemBind.tvTitle.visibility = View.VISIBLE
            holder.itemBind.tvNote.visibility = View.VISIBLE
            holder.itemBind.imageView.visibility = View.VISIBLE

            holder.itemBind.tvTitle.text = currentNote.title
            holder.itemBind.tvNote.text = currentNote.noteContent

            if (currentNote.imagePath != "") {
                Glide.with(context).load(currentNote.imagePath).error(R.drawable.placeholder_image)
                    .override(100, 100).fitCenter().centerCrop()
                    .placeholder(R.drawable.placeholder_image).into(holder.itemBind.imageView)
                holder.itemBind.imageView.visibility = View.VISIBLE

            } else
                holder.itemBind.imageView.visibility = View.GONE
        }
    }

    private fun setNoteItemListeners(currentNote: Note, holder : NoteViewHolder){
        //implement logic for click on note item
        holder.itemView.setOnClickListener {
            if(currentNote.locked)
                (context as MainActivity).showPasswordDialog(false,true, currentNote)
            else
                goToNoteActivity(currentNote)
        }

        //implement logic for long click on note item
        holder.itemView.setOnLongClickListener {
            var bottomSheetMain = BottomSheetMain(currentNote)
            bottomSheetMain.show(fragmentManager, "bottomSheetMain")

            true
        }
    }

    fun goToNoteActivity(currentNote : Note){
        val intent = Intent(context, NoteActivity::class.java).apply {
            putExtra(MyConstants.NOTE, currentNote)
        }
        context.startActivity(intent)
    }

    private fun getCurrentDateTime(timeMillis : Long): String {
        val instant = Instant.ofEpochMilli(timeMillis)
        val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy HH:mm:ss", Locale.ENGLISH)
        return dateTime.format(formatter)
    }
}