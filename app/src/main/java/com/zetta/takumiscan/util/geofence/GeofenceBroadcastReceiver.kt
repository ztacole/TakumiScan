package com.zetta.takumiscan.util.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.zetta.takumiscan.util.notification.NotificationHelper

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("Pending Intent", "onReceive: Intent received")
        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        if (geofencingEvent == null){
            Log.e("GeofenceBroadcastReceiver", "GeofencingEvent is null")
            return
        }

        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
            Log.e("GeofenceBroadcastReceiver", "Error: $errorMessage")
            return
        }

        when (geofencingEvent.geofenceTransition){
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                (context as? OnGeofenceTriggeredListener)?.onGeofenceEnter()
                Log.d("receiver", "onReceive: Entered geofence")
                NotificationHelper.showNotification(
                    context,
                    "Selamat Datang!",
                    "Jangan lupa absen yaa!!"
                )
            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                (context as? OnGeofenceTriggeredListener)?.onGeofenceExit()
                Log.d("receiver", "onReceive: Exited geofence")
                NotificationHelper.showNotification(
                    context,
                    "Hati-hati Dijalan!",
                    "Terima kasih untuk hari ini yaa!!"
                )
            }
            else -> {
                (context as? OnGeofenceTriggeredListener)?.onGeofenceExit()
                Log.e("receiver", "onReceive: Unknown geofence transition")
            }
        }
    }
}