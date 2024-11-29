package com.zetta.takumiscan.util.geofence

// GeofenceBroadcastReceiver.kt
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Toast.makeText(context, "Sukses", Toast.LENGTH_SHORT).show()
    }
}