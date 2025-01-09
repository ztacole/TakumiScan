package com.zetta.takumiscan.presentation.auth

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.text.InputType
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.zetta.takumiscan.R
import com.zetta.takumiscan.data.local.DBHelper
import com.zetta.takumiscan.presentation.main.MainActivity
import com.zetta.takumiscan.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var dbHelper: DBHelper

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        dbHelper = DBHelper(this)
        enableEdgeToEdge()
        setContentView(binding.root)

        initListener()
    }

    private fun initListener(){
        binding.btnLogin.setOnClickListener {
            loginProcess()
        }

        binding.icPassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible

            if (isPasswordVisible){
                binding.tbPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.icPassword.setImageResource(R.drawable.hidden_password)
            }
            else{
                binding.tbPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.icPassword.setImageResource(R.drawable.show_password)
            }

            binding.tbPassword.setSelection(binding.tbPassword.text.length)
        }
    }

    private fun loginProcess(){
        val nisn = binding.tbNISN.text
        val password = binding.tbPassword.text
        if (nisn.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Data tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }
        val data = dbHelper.getDataUser()
        if (data.nisn != nisn.toString()){
            Toast.makeText(this, "User tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }
        if (data.password != password.toString()){
            Toast.makeText(this, "Password salah", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}