package com.zetta.takumiscan.presentation.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.zetta.takumiscan.R
import com.zetta.takumiscan.presentation.auth.LoginActivity
import com.zetta.takumiscan.presentation.auth.RegisterActivity
import com.zetta.takumiscan.presentation.onboarding.OnBoardingActivity
import com.zetta.takumiscan.util.CacheController

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var cacheController: CacheController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        cacheController = CacheController(this)

        Handler(Looper.getMainLooper()).postDelayed({
            var intent = Intent(this, OnBoardingActivity::class.java)
            if (cacheController.isAlreadyOpen()) intent = Intent(this, RegisterActivity::class.java)
            if (cacheController.isAlreadyRegister()) intent = Intent(this, LoginActivity::class.java)

            startActivity(intent)
            finish()
        }, 2000)
    }
}