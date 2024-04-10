package com.example.notespro

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.notespro.databinding.FragmentBottomSheetNoteBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetNote(val isLocked : Boolean) : BottomSheetDialogFragment() {

    private lateinit var bind : FragmentBottomSheetNoteBinding
    private val activityParent get() = activity as NoteActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bind = FragmentBottomSheetNoteBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setLockUnlockOption()
        setupBottomSheetListeners()
    }

    private fun setLockUnlockOption(){
        if(isLocked){
            bind.lockUnlockIcon.setImageResource(R.drawable.unlock_icon)
            bind.lockUnlockText.setText("Unlock Note")
        }
        else{
            bind.lockUnlockIcon.setImageResource(R.drawable.lock_icon)
            bind.lockUnlockText.setText("Lock Note")
        }
    }

    private fun setupBottomSheetListeners()
    {
        bind.insertImage.setOnClickListener{
            activityParent.onSelectOption(1)
            dismiss()
        }

        bind.saveNote.setOnClickListener{
            activityParent.onSelectOption(2)
            dismiss()
        }

        bind.deleteNote.setOnClickListener{
            activityParent.onSelectOption(3)
            dismiss()
        }

        bind.lockUnlockNote.setOnClickListener{
            activityParent.onSelectOption(4)
            dismiss()
        }
    }
}