package com.zetta.takumiscan.presentation.main.menu.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
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
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.zetta.takumiscan.data.local.DBHelper
import com.zetta.takumiscan.databinding.FragmentHomeBinding
import com.zetta.takumiscan.model.GeofenceData
import com.zetta.takumiscan.model.Note
import com.zetta.takumiscan.presentation.main.MainActivity
import com.zetta.takumiscan.presentation.main.menu.home.notes.NotesActivity
import com.zetta.takumiscan.presentation.main.menu.home.notes.NotesSmallAdapter
import com.zetta.takumiscan.presentation.main.menu.home.notes.NotesDetailActivity
import com.zetta.takumiscan.presentation.main.menu.scan.ScanActivity
import com.zetta.takumiscan.util.core.Constants.GEOFENCE_ID
import com.zetta.takumiscan.util.core.Constants.GEOFENCE_LATITUDE
import com.zetta.takumiscan.util.core.Constants.GEOFENCE_LONGITUDE
import com.zetta.takumiscan.util.core.Constants.GEOFENCE_RADIUS
import com.zetta.takumiscan.util.geofence.GeofenceHelper
import com.zetta.takumiscan.util.geofence.OnGeofenceTriggeredListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class HomeFragment : Fragment() {
    private lateinit var geofencingClient: GeofencingClient
    private val geofenceList = mutableListOf<GeofenceData>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var main: MainActivity

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false

        if (locationGranted) {
            startLocationUpdates()
        }
    }
    private lateinit var binding: FragmentHomeBinding
    private lateinit var dbHelper: DBHelper
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var geofenceTriggeredListener: OnGeofenceTriggeredListener

    private lateinit var notes: List<Note>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)
        dbHelper = DBHelper(requireContext())
        main = (requireActivity() as MainActivity)
        geofenceTriggeredListener = (requireContext() as OnGeofenceTriggeredListener)

        setup()

        startClock()
        requestPermission()
        setNotesData()

        binding.btnSetLocation.setOnClickListener{
            binding.lblLocation.text = "Sedang melacak lokasi.."
            Toast.makeText(main, "Mohon tunggu, Sedang melacak lokasi..", Toast.LENGTH_SHORT).show()
            main.binding.btnQR.setOnClickListener {
                Toast.makeText(main, "Mohon tunggu, Sedang melacak lokasi..", Toast.LENGTH_SHORT).show()
            }
            requestPermission()
        }

        if (isAlreadyPresence()) binding.lblKeterangan.text = "Yeayy, kamu sudah absen untuk hari ini\nTetap semangat dalam belajar yaa!"

        binding.tbAddNote.setOnClickListener {
            Intent(requireContext(), NotesDetailActivity::class.java).also {
                startActivity(it)
            }
        }

        binding.lblShowNotes.setOnClickListener {
            Intent(requireContext(), NotesActivity::class.java).also {
                startActivity(it)
            }
        }

        return binding.root
    }

    private fun setup(){
        main.binding.btnQR.setOnClickListener {
            Toast.makeText(main, "Mohon tunggu, Sedang melacak lokasi..", Toast.LENGTH_SHORT).show()
        }
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION), 1)
        }


        geofenceList.add(
            GeofenceData
                (
                GEOFENCE_ID,
                GEOFENCE_LATITUDE,
                GEOFENCE_LONGITUDE,
                GEOFENCE_RADIUS,
                Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT
            )
        )
        geofencingClient = LocationServices.getGeofencingClient(requireContext())
        GeofenceHelper.addGeofences(requireContext(), geofencingClient, geofenceList){
            if (it) Log.d("Geofence", "onLocationResult: Success")
            else Log.d("Geofence", "onLocationResult: Failure")
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let {
                    checkUserLocation(it)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
        setNotesData()
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            )
        }
        else{
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
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

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            Toast.makeText(requireContext(), "Izin lokasi diperlukan", Toast.LENGTH_SHORT).show()
            return
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000L
        ).build()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
    }

    private fun isAlreadyPresence(): Boolean{
        val histories = dbHelper.getListHistory()
        if (histories.isEmpty()) return false
        val newestHistory = histories[0]

        val calendar = Calendar.getInstance()
        val dateTime = android.icu.text.SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
            .parse(newestHistory.dateTime)
        calendar.time = dateTime

        val date = calendar.get(Calendar.DATE)
        val currentDate = Calendar.getInstance().get(Calendar.DATE)

        return date == currentDate
    }

    private fun checkUserLocation(userLocation: Location) {
        val targetLocation = Location("target")
        targetLocation.latitude = GEOFENCE_LATITUDE
        targetLocation.longitude = GEOFENCE_LONGITUDE

        val distance = userLocation.distanceTo(targetLocation)

        if (distance <= GEOFENCE_RADIUS){
            binding.lblLocation.text = "SMKN 24 Jakarta"
            binding.lblDenah.visibility = View.VISIBLE
            binding.scrollDenah.visibility = View.VISIBLE
            geofenceTriggeredListener.onGeofenceEnter()
            Log.d("User Location", "checkUserLocation: Inside Geofence")
        }
        else{
            binding.lblLocation.text = "$distance m dari SMKN 24 Jakarta"
            binding.lblDenah.visibility = View.GONE
            binding.scrollDenah.visibility = View.GONE
            geofenceTriggeredListener.onGeofenceExit()
            Log.d("User Location", "checkUserLocation: Outside Geofence")
        }

        if (isAlreadyPresence()) {
            main.binding.btnQR.setOnClickListener {
                Toast.makeText(main, "Kamu sudah absen", Toast.LENGTH_SHORT).show()
            }
            binding.lblKeterangan.text = "Yeayy, kamu sudah absen untuk hari ini\nTetap semangat dalam belajar yaa!"
        }
    }

    private fun setNotesData(){
        binding.rvNotes.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        notes = dbHelper.getListNotes()
        if (notes.isEmpty()){
            binding.lblEmpty.visibility = View.VISIBLE
            binding.rvNotes.visibility = View.INVISIBLE
        }
        else{
            binding.lblEmpty.visibility = View.INVISIBLE
            binding.rvNotes.visibility = View.VISIBLE
            binding.rvNotes.adapter = NotesSmallAdapter(notes)
        }
    }
}