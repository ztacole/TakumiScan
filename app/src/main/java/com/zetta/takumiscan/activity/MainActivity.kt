package com.zetta.takumiscan.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.zetta.takumiscan.R
import com.zetta.takumiscan.databinding.ActivityMainBinding
import com.zetta.takumiscan.fragment.HomeFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = resources.getColor(R.color.navy, theme)

        setFragment(HomeFragment())

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> setFragment(HomeFragment())

            }
            true
        }

        binding.btnQR.setOnClickListener {
            Intent(this, ScanActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    public fun setFragment(f: Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(binding.frame.id, f)
            addToBackStack(null)
            commit()
        }
    }
}