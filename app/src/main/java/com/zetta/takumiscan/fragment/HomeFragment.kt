package com.zetta.takumiscan.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zetta.takumiscan.R
import com.zetta.takumiscan.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)

        startClock()

        return binding.root
    }

    private fun startClock() {
        handler.post(object : Runnable {
            override fun run() {
                getCurrentTimeInWIB()
                handler.postDelayed(this, 1000)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    fun getCurrentTimeInWIB() {
        val utcFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        utcFormat.timeZone = TimeZone.getTimeZone("UTC")

        val wibFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        wibFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")

        val utcFormatSecond = SimpleDateFormat(":ss", Locale.getDefault())
        utcFormatSecond.timeZone = TimeZone.getTimeZone("UTC")

        val wibFormatSecond = SimpleDateFormat(":ss", Locale.getDefault())
        wibFormatSecond.timeZone = TimeZone.getTimeZone("Asia/Jakarta")

        val utcTime = utcFormat.parse(utcFormat.format(Date()))
        val utcSecond = utcFormatSecond.parse(utcFormatSecond.format(Date()))

        binding.lblHourMinute.text = wibFormat.format(utcTime!!)
        binding.lblSecond.text = wibFormatSecond.format(utcSecond!!)
    }
}