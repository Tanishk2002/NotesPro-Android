package com.example.notespro

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.notespro.Model.Note
import com.example.notespro.databinding.FragmentBottomSheetMainBinding
import com.example.notespro.databinding.FragmentBottomSheetNoteBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetMain(val selectedNote : Note) : BottomSheetDialogFragment() {

    private lateinit var bind : FragmentBottomSheetMainBinding
    private val activityParent get() = activity as MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bind = FragmentBottomSheetMainBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setLockUnlockOption()
        setupBottomSheetListeners()
    }

    private fun setLockUnlockOption(){
        if(selectedNote.locked){
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
        bind.editNote.setOnClickListener{
            activityParent.onSelectOption(1, selectedNote)
            dismiss()
        }

        bind.deleteNote.setOnClickListener{
            activityParent.onSelectOption(2, selectedNote)
            dismiss()
        }

        bind.lockUnlockNote.setOnClickListener{
            activityParent.onSelectOption(3, selectedNote)
            dismiss()
        }
    }
}