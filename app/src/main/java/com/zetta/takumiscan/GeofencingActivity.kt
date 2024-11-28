package com.zetta.takumiscan

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.zetta.takumiscan.GeofenceHelper
import com.zetta.takumiscan.R

class GeofencingActivity : AppCompatActivity() {

    private lateinit var geofencingClient: GeofencingClient
    private val geofenceList = mutableListOf<Geofence>()
    private val GEOFENCE_ID = "MY_GEOFENCE_ID"
    private val GEOFENCE_RADIUS = 100f // in meters
    private val GEOFENCE_LATITUDE = -6.321370280496017
    private val GEOFENCE_LONGITUDE = 106.89955842050071

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geofencing)

        geofencingClient = LocationServices.getGeofencingClient(this)

        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false

            if (locationGranted) {
                checkLocation()
            } else {

            }
        }
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )

        findViewById<TextView>(R.id.titleTextView).setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    100
                )
                return@setOnClickListener
            }
        }
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