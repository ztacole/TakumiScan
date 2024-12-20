package com.zetta.takumiscan.presentation.main.menu.home.notes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.marginBottom
import androidx.recyclerview.widget.RecyclerView
import com.zetta.takumiscan.databinding.ItemNoteBinding
import com.zetta.takumiscan.model.Note

class NotesAdapter(private val listNotes: List<Note>): RecyclerView.Adapter<NotesAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemNoteBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return listNotes.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = listNotes[position]
        holder.binding.lblTitle.text = note.title ?: "Tak Berjudul"
        holder.binding.lblNote.text = note.notes
    }
}