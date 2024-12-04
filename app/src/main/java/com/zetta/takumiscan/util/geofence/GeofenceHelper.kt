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
    // Data kelas untuk menyimpan informasi geofence


    // Membuat geofence
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

    // Membuat request geofencing
    private fun getGeofencingRequest(geofences: List<Geofence>): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(geofences)
        }.build()
    }

    // Membuat pending intent untuk broadcast receiver
    fun getGeofencePendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    // Menambahkan geofence
    fun addGeofences(
        context: Context,
        geofencingClient: GeofencingClient,
        geofenceDataList: List<GeofenceData>,
        callback: (Boolean) -> Unit
    ) {
        // Buat daftar geofence dari data
        val geofences = geofenceDataList.map { createGeofence(it) }

        // Cek izin
        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            callback(false)
            return
        }

        // Tambahkan geofence
        try{
            geofencingClient.addGeofences(
                getGeofencingRequest(geofences),
                getGeofencePendingIntent(context)
            ).addOnCompleteListener{
                if (it.isSuccessful){
                    Log.d("Geo", "add: success")
                }
                else{
                    Log.e("Geo", "add: ${it.exception?.message}")
                }
            }
            Log.d("FailedGeofence", "addGeofences: Success add", )
        }
        catch (e:SecurityException){
            Log.e("FailedGeofence", "addGeofences: ${e.message}", )
        }
    }
}