package com.zetta.takumiscan.presentation.main.menu.history

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zetta.takumiscan.R
import com.zetta.takumiscan.databinding.ItemHistoryBinding
import com.zetta.takumiscan.model.History
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class HistoryAdapter(private val histories: List<History>): RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemHistoryBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return histories.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val history = histories[position]
        val photo = BitmapFactory.decodeByteArray(history.photo, 0, history.photo.size)
        val format = SimpleDateFormat("EEE, dd MMM yyyy, HH:mm 'WIB'", Locale("id", "ID"))
        format.timeZone = TimeZone.getTimeZone("GMT+07:00")
        val convertDateTime = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH).parse(history.dateTime)
        val dateTime = convertDateTime?.let { format.format(it) }

        holder.binding.imgHistory.setImageBitmap(photo)
        holder.binding.lblDate.text = dateTime
        holder.binding.lblStatus.text = history.status
        when(history.mood){
            "Senang"-> holder.binding.iconMood.setImageResource(R.drawable.mood_smile)
            "Datar"-> holder.binding.iconMood.setImageResource(R.drawable.mood_flat)
            "Sedih"-> holder.binding.iconMood.setImageResource(R.drawable.mood_sad)
        }
    }
}