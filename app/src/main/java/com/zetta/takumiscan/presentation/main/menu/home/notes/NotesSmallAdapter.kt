package com.zetta.takumiscan.presentation.main.menu.home.notes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zetta.takumiscan.databinding.ItemNoteSmallBinding
import com.zetta.takumiscan.model.Note

class NotesSmallAdapter(private val listNotes: List<Note>): RecyclerView.Adapter<NotesSmallAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemNoteSmallBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemNoteSmallBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return 3
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = listNotes[position]
        holder.binding.lblTitle.text = note.title ?: "Tak Berjudul"
        holder.binding.lblNote.text = note.notes
    }
}