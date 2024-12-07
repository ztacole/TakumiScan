package com.zetta.takumiscan.presentation.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.zetta.takumiscan.R
import com.zetta.takumiscan.presentation.main.menu.scan.ScanActivity
import com.zetta.takumiscan.databinding.ActivityMainBinding
import com.zetta.takumiscan.presentation.main.menu.history.HistoryFragment
import com.zetta.takumiscan.presentation.main.menu.home.HomeFragment

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val listFragment = listOf(
        HomeFragment(),
        HistoryFragment()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = getColor(R.color.navy)

        binding.pager.isUserInputEnabled = false

        binding.pager.adapter = object : FragmentStateAdapter(this){
            override fun getItemCount(): Int {
                return listFragment.size
            }

            override fun createFragment(position: Int): Fragment {
                return listFragment[position]
            }
        }

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

        binding.btnQR.setOnClickListener {
            Intent(this, ScanActivity::class.java).also {
                startActivity(it)
            }
        }
    }
}