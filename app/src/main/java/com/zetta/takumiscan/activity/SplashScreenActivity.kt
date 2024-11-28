package com.zetta.takumiscan.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.zetta.takumiscan.GeofencingActivity
import com.zetta.takumiscan.R

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, GeofencingActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000) // Delay 2 detik
    }
}