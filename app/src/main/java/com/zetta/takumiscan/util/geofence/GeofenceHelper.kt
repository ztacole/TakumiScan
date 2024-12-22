package com.zetta.takumiscan.util.geofence

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.zetta.takumiscan.model.GeofenceData

object GeofenceHelper {
    private fun createGeofence(data: GeofenceData): Geofence {
        return Geofence.Builder()
            .setRequestId(data.id)
            .setCircularRegion(
                data.latitude,
                data.longitude,
                data.radius
            )
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(data.transitionTypes)
            .build()
    }

    private fun getGeofencingRequest(geofences: List<Geofence>): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER or GeofencingRequest.INITIAL_TRIGGER_EXIT)
            addGeofences(geofences)
        }.build()
    }

    private fun getGeofencePendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun addGeofences(
        context: Context,
        geofencingClient: GeofencingClient,
        geofenceDataList: List<GeofenceData>,
        callback: (Boolean) -> Unit
    ) {
        val geofences = geofenceDataList.map { createGeofence(it) }
        Log.d("GeofenceHelper", "Geofences: $geofences")

        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            callback(false)
            return
        }

        geofencingClient.addGeofences(
            getGeofencingRequest(geofences),
            getGeofencePendingIntent(context)
        ).run {
            addOnSuccessListener {
                Log.d("ChildFragment", "Geofences added")
            }
            addOnFailureListener {
                Log.e("ChildFragment", "Failed to add geofences")
            }
        }
    }
}