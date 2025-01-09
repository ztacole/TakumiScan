package com.zetta.takumiscan.presentation.main.menu.history

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zetta.takumiscan.R
import com.zetta.takumiscan.databinding.ItemHistoryBinding
import com.zetta.takumiscan.databinding.ItemNoteBinding
import com.zetta.takumiscan.databinding.ItemNoteHeaderBinding
import com.zetta.takumiscan.model.History
import com.zetta.takumiscan.util.core.CoreFunction.dpToPx
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class HistoryAdapter(private val histories: List<History>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_HEADER = 0
    private val VIEW_TYPE_ITEM = 1

    inner class HeaderViewHolder(val binding: ItemNoteHeaderBinding): RecyclerView.ViewHolder(binding.root)
    inner class ItemViewHolder(val binding: ItemHistoryBinding): RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER){
            val view = ItemNoteHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            HeaderViewHolder(view)
        }
        else{
            val view = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ItemViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return histories.size + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams

        if (position == histories.size) layoutParams.bottomMargin = holder.itemView.context.dpToPx(96)
        else layoutParams.bottomMargin = holder.itemView.context.dpToPx(0)

        if (position > 1) layoutParams.topMargin = holder.itemView.context.dpToPx(8)

        if (position > 0) {
            val view = (holder as ItemViewHolder)

            val history = histories[position - 1]
            val photo = BitmapFactory.decodeByteArray(history.photo, 0, history.photo.size)
            val format = SimpleDateFormat("EEE, dd MMM yyyy, HH:mm 'WIB'", Locale("id", "ID"))
            format.timeZone = TimeZone.getTimeZone("GMT+07:00")
            val convertDateTime = SimpleDateFormat(
                "EEE MMM dd HH:mm:ss zzz yyyy",
                Locale.ENGLISH
            ).parse(history.dateTime)
            val dateTime = convertDateTime?.let { format.format(it) }

            view.binding.imgHistory.setImageBitmap(photo)
            view.binding.lblDate.text = dateTime
            view.binding.lblStatus.text = history.status
            when (history.mood) {
                "Senang" -> view.binding.iconMood.setImageResource(R.drawable.mood_smile)
                "Datar" -> view.binding.iconMood.setImageResource(R.drawable.mood_flat)
                "Sedih" -> view.binding.iconMood.setImageResource(R.drawable.mood_sad)
            }
        }
        else layoutParams.topMargin = holder.itemView.context.dpToPx(16)
    }
}