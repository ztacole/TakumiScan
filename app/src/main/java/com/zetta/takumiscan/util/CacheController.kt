package com.zetta.takumiscan.util

import android.content.Context

class CacheController(private val context: Context) {
    private val spStatus = context.getSharedPreferences("status", Context.MODE_PRIVATE)

    fun setStatus(status: String) = spStatus.edit().putString("status", status).apply()

    fun getStatus(): String = spStatus.getString("status", "Opening").toString()

    fun isAlreadyOpen(): Boolean = getStatus() == "Not Registered"

    fun isAlreadyRegister(): Boolean = getStatus() == "Registered"
}