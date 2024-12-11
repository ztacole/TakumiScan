package com.zetta.takumiscan.presentation.main.menu.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.zetta.takumiscan.data.local.DBHelper
import com.zetta.takumiscan.databinding.FragmentHistoryBinding

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

    fun initialize(){
        val histories = dbHelper.getListHistory()
        val status = dbHelper.getStatus()

        binding.rvHistory.adapter = HistoryAdapter(histories)
        binding.lblTepatWaktu.text = status[0].toString()
        binding.lblTerlambat.text = status[1].toString()
    }
}