package com.zetta.takumiscan.util.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent == null) {
            Log.e("GeofenceReceiverNull", "Error: geofencingEvent is null")
            return
        }

        if (geofencingEvent.hasError()) {
            Log.e("GeofenceReceiver", "Error: ${geofencingEvent.errorCode}")
            return
        }

        val geofenceTransition = geofencingEvent.geofenceTransition
        val transitionMessage = when (geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> "Masuk ke dalam geofence"
            Geofence.GEOFENCE_TRANSITION_EXIT -> "Keluar dari geofence"
            else -> "Perubahan geofence tidak diketahui"
        }

        Toast.makeText(context, transitionMessage, Toast.LENGTH_SHORT).show()
        Log.i("Tes", "onReceive: $transitionMessage")
    }
}