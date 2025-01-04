package com.zetta.takumiscan.presentation.main

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.zetta.takumiscan.R
import com.zetta.takumiscan.data.local.DBHelper
import com.zetta.takumiscan.presentation.main.menu.scan.ScanActivity
import com.zetta.takumiscan.databinding.ActivityMainBinding
import com.zetta.takumiscan.model.History
import com.zetta.takumiscan.presentation.main.menu.history.HistoryFragment
import com.zetta.takumiscan.presentation.main.menu.home.HomeFragment
import com.zetta.takumiscan.presentation.main.profile.ProfileActivity
import com.zetta.takumiscan.util.geofence.OnGeofenceTriggeredListener

class MainActivity : AppCompatActivity(){
    lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: DBHelper
    private lateinit var histories: List<History>

    private val listFragment = listOf(
        HomeFragment(),
        HistoryFragment()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        dbHelper = DBHelper(this)
        histories = dbHelper.getListHistory()
        setContentView(binding.root)
        window.statusBarColor = getColor(R.color.navy)

        setUI()
        setNavigation()

        initListener()
    }

    private fun initListener(){
        binding.btnQR.setOnClickListener {
            Toast.makeText(this, "Mohon tunggu, Sedang melacak lokasi..", Toast.LENGTH_SHORT).show()
        }

        binding.imgProfile.setOnClickListener {
            Intent(this, ProfileActivity::class.java).also { startActivity(it) }
        }
    }

    override fun onResume() {
        super.onResume()
        val currentHistories = dbHelper.getListHistory()
        if (histories.size != currentHistories.size) binding.pager.currentItem = 1
        setUI()
    }

    private fun setUI(){
        val data = dbHelper.getDataUser()
        val profile = BitmapFactory.decodeByteArray(data.photo, 0, data.photo.size)

        binding.imgProfile.setImageBitmap(profile)
        binding.lblNama.text = data.nama
        binding.lblKelas.text = data.kelas
    }

    private fun setNavigation(){
        binding.pager.isUserInputEnabled = false

        binding.pager.adapter = object : FragmentStateAdapter(this){
            override fun getItemCount(): Int {
                return listFragment.size
            }

            override fun createFragment(position: Int): Fragment {
                return listFragment[position]
            }
        }

        binding.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                if (binding.pager.currentItem == 1) binding.bottomNavigationView.selectedItemId = R.id.history
                else binding.bottomNavigationView.selectedItemId = R.id.home
            }
        })

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId){
                R.id.home -> binding.pager.currentItem = 0
                R.id.history -> binding.pager.currentItem = 1
                else -> {
                    return@setOnItemSelectedListener false
                }
            }
            true
        }

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if (binding.pager.currentItem != 0) {
                    supportFragmentManager.popBackStack()
                    binding.bottomNavigationView.selectedItemId = R.id.home
                } else {
                    finish()
                }
            }
        })
    }
}