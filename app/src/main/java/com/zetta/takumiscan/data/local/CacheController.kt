package com.zetta.takumiscan.data.local

import android.content.Context

class CacheController(private val context: Context) {
    private val spStatus = context.getSharedPreferences("status", Context.MODE_PRIVATE)
    private val spNotes = context.getSharedPreferences("notes", Context.MODE_PRIVATE)

    //Status
    fun setStatus(status: String) = spStatus.edit().putString("status", status).apply()

    private fun getStatus(): String = spStatus.getString("status", "Opening").toString()

    fun isFirstTime(): Boolean = getStatus() == "Not Registered"

    fun isAlreadyRegister(): Boolean = getStatus() == "Registered"

    //Notes
    fun alreadyOpenedNotes() = spNotes.edit().putString("status", "Opened").apply()

    private fun getStatusNotes(): String = spNotes.getString("status", "First Time").toString()

    fun isFirstTimeOpenNotes(): Boolean = getStatusNotes() == "Opened"
}