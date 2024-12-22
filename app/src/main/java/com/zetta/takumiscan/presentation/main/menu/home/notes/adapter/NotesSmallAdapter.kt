package com.zetta.takumiscan.presentation.main.menu.home.notes.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zetta.takumiscan.databinding.ItemNoteSmallBinding
import com.zetta.takumiscan.model.Note
import com.zetta.takumiscan.presentation.main.menu.home.notes.NotesDetailActivity

class NotesSmallAdapter(private val listNotes: List<Note>): RecyclerView.Adapter<NotesSmallAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemNoteSmallBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemNoteSmallBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return if (listNotes.size > 3) 3
        else listNotes.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = listNotes[position]
        holder.binding.lblTitle.text = note.title ?: "Tak Berjudul"
        holder.binding.lblNote.text = note.notes
        holder.itemView.setOnClickListener {
            Intent(holder.itemView.context, NotesDetailActivity::class.java).also {
                it.putExtra("id", note.id)
                holder.itemView.context.startActivity(it)
            }
        }
    }
}