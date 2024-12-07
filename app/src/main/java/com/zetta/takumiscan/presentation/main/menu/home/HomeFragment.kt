package com.zetta.takumiscan.presentation.main.menu.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.zetta.takumiscan.databinding.FragmentHomeBinding
import com.zetta.takumiscan.model.GeofenceData
import com.zetta.takumiscan.util.Constants.GEOFENCE_ID
import com.zetta.takumiscan.util.Constants.GEOFENCE_LATITUDE
import com.zetta.takumiscan.util.Constants.GEOFENCE_LONGITUDE
import com.zetta.takumiscan.util.Constants.GEOFENCE_RADIUS
import com.zetta.takumiscan.util.geofence.GeofenceHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class HomeFragment : Fragment() {
    private lateinit var geofencingClient: GeofencingClient
    private val geofenceList = mutableListOf<GeofenceData>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false

        if (locationGranted) {
            checkLocation()
        }
    }
    private lateinit var binding: FragmentHomeBinding
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)

        geofenceList.add(GeofenceData(
            GEOFENCE_ID,
            GEOFENCE_LATITUDE,
            GEOFENCE_LONGITUDE,
            GEOFENCE_RADIUS,
            Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT
        ))
        geofencingClient = LocationServices.getGeofencingClient(requireActivity())

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations){
                    GeofenceHelper.addGeofences(requireContext(), geofencingClient, geofenceList){ success ->
                        if (success) Log.d("Geofence", "onLocationResult: Success")
                        else Log.e("Geofence", "onLocationResult: Failure")
                    }
                }
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (savedInstanceState == null) {
            startClock()
            setLocation()
        }

        binding.btnSetLocation.setOnClickListener{
            setLocation()
        }

        return binding.root
    }

    private fun setLocation(){
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        )
    }

    private fun startClock() {
        handler.post(object : Runnable {
            override fun run() {
                getCurrentTimeInWIB()
                handler.postDelayed(this, 1000)
            }
        })
    }

    private fun getCurrentTimeInWIB() {
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

    private fun checkLocation() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(requireContext(), "Izin lokasi diperlukan", Toast.LENGTH_SHORT).show()
            return
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000L
        ).build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

//    private fun isUserInsideGeofence(lat: Double, lon: Double): Boolean {
//        val distance = FloatArray(1)
//        Location.distanceBetween(
//            lat, lon,
//            GEOFENCE_LATITUDE, GEOFENCE_LONGITUDE,
//            distance
//        )
//        return distance[0] <= GEOFENCE_RADIUS
//    }
}