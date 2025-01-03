package com.zetta.takumiscan.presentation.main.menu.history

import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.zetta.takumiscan.data.local.DBHelper
import com.zetta.takumiscan.databinding.FragmentHistoryBinding
import java.util.Calendar
import java.util.Locale

class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var dbHelper: DBHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dbHelper = DBHelper(requireContext())
        binding = FragmentHistoryBinding.inflate(layoutInflater)

        binding.rvHistory.layoutManager = object : LinearLayoutManager(requireContext()){
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        initialize()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initialize()
    }

    private fun initialize(){
        var histories = dbHelper.getListHistory()
        val status = dbHelper.getStatus()
        if (histories.isEmpty()) return

        val history = histories[histories.size-1]
        if (isMonthGreaterThanCurrent(history.dateTime)) {
            dbHelper.deleteAllHistories()
            histories = dbHelper.getListHistory()
        }

        binding.rvHistory.adapter = HistoryAdapter(histories)
        binding.lblTepatWaktu.text = status[0].toString()
        binding.lblTerlambat.text = status[1].toString()
    }

    private fun isMonthGreaterThanCurrent(dateString: String): Boolean{
        val calendar = Calendar.getInstance()
        val date = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH).parse(dateString)
        calendar.time = date

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)

        return if (currentYear == year) currentMonth > month
        else currentYear > year
    }
}