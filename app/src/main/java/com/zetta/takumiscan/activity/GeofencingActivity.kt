package com.zetta.takumiscan.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.zetta.takumiscan.R
import com.zetta.takumiscan.util.Utils.GEOFENCE_LATITUDE
import com.zetta.takumiscan.util.Utils.GEOFENCE_LONGITUDE
import com.zetta.takumiscan.util.Utils.GEOFENCE_RADIUS

class GeofencingActivity : AppCompatActivity() {
    private lateinit var geofencingClient: GeofencingClient
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false

        if (locationGranted) {
            checkLocation()
        } else {
            checkLocation()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geofencing)

        geofencingClient = LocationServices.getGeofencingClient(this)

        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }

    private fun checkLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this, "Lokasi Diperlukan", Toast.LENGTH_SHORT).show()
            return
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val userLatitude = location.latitude
                val userLongitude = location.longitude

                if (isUserInsideGeofence(userLatitude, userLongitude)) {
                    Toast.makeText(
                        this,
                        "Pengguna berada di tempat yang ditentukan",
                        Toast.LENGTH_SHORT
                    ).show()
                    Intent(this, MainActivity::class.java).also {
                        startActivity(it)
                        finish()
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Pengguna tidak berada di tempat yang ditentukan",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun isUserInsideGeofence(lat: Double, lon: Double): Boolean {
        val distance = FloatArray(1)
        Location.distanceBetween(
            lat, lon,
            GEOFENCE_LATITUDE, GEOFENCE_LONGITUDE,
            distance
        )
        return distance[0] <= GEOFENCE_RADIUS
    }
}