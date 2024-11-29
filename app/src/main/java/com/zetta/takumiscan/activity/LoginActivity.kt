package com.zetta.takumiscan.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zetta.takumiscan.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            loginProcess()
        }
    }

    private fun loginProcess(){
        if (binding.tbEmail.text.isEmpty() || binding.tbPass.text.isEmpty()){
            Toast.makeText(this, "Data tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(this, GeofencingActivity::class.java)
        startActivity(intent)
        finish()
    }
}