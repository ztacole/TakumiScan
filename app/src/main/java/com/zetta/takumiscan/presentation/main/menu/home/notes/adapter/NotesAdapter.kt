package com.zetta.takumiscan.presentation.main.menu.home.notes.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zetta.takumiscan.databinding.ItemNoteBinding
import com.zetta.takumiscan.databinding.ItemNoteHeaderBinding
import com.zetta.takumiscan.model.Note
import com.zetta.takumiscan.presentation.main.menu.home.notes.NotesDetailActivity
import com.zetta.takumiscan.util.core.CoreFunction.dpToPx

class NotesAdapter(private val listNotes: List<Note>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val VIEW_TYPE_HEADER = 0
    private val VIEW_TYPE_ITEM = 1

    inner class HeaderViewHolder(val binding: ItemNoteHeaderBinding): RecyclerView.ViewHolder(binding.root)
    inner class ItemViewHolder(val binding: ItemNoteBinding): RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER){
            val view = ItemNoteHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            HeaderViewHolder(view)
        }
        else{
            val view = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ItemViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return listNotes.size + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position > 0) {
            val note = listNotes[position - 1]
            val view = (holder as ItemViewHolder)
            val layoutParams = view.itemView.layoutParams as ViewGroup.MarginLayoutParams
            if (position == listNotes.size){
                layoutParams.bottomMargin = view.itemView.context.dpToPx(96)
            }
            else{
                layoutParams.bottomMargin = view.itemView.context.dpToPx(0)
            }
            view.binding.lblTitle.text = note.title ?: "Tak Berjudul"
            view.binding.lblNote.text = note.notes
            view.itemView.setOnClickListener {
                Intent(view.itemView.context, NotesDetailActivity::class.java).also {
                    it.putExtra("id", note.id)
                    view.itemView.context.startActivity(it)
                }
            }
        }
    }
}