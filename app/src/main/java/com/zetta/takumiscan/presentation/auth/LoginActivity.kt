package com.zetta.takumiscan.presentation.auth

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zetta.takumiscan.data.local.DBHelper
import com.zetta.takumiscan.presentation.main.MainActivity
import com.zetta.takumiscan.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        dbHelper = DBHelper(this)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            loginProcess()
        }

        binding.iconShowPassword.setOnClickListener {
//            if (binding.tbPass.inputType == InputType.TYPE_TEXT_VARIATION_PASSWORD) binding.tbPass.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
//            else binding.tbPass.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
    }

    private fun loginProcess(){
        if (binding.tbNISN.text.isEmpty() || binding.tbPass.text.isEmpty()){
            Toast.makeText(this, "Data tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }
        val data = dbHelper.getDataUser()
        if (data.nisn != binding.tbNISN.text.toString()){
            Toast.makeText(this, "User tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }
        if (data.password != binding.tbPass.text.toString()){
            Toast.makeText(this, "Password salah", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}