package com.zetta.takumiscan.presentation.main.menu.home.notes.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zetta.takumiscan.databinding.ItemNoteBinding
import com.zetta.takumiscan.model.Note
import com.zetta.takumiscan.presentation.main.menu.home.notes.NotesDetailActivity
import com.zetta.takumiscan.util.core.CoreFunction.dpToPx

class NotesAdapter(private val listNotes: List<Note>): RecyclerView.Adapter<NotesAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemNoteBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return listNotes.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = listNotes[position]
        val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
        if (position == listNotes.size) {
            layoutParams.bottomMargin = holder.itemView.context.dpToPx(96)
        } else {
            layoutParams.bottomMargin = holder.itemView.context.dpToPx(0)
        }
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