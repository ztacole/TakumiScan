package com.zetta.takumiscan.presentation.auth

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.zetta.takumiscan.data.local.DBHelper
import com.zetta.takumiscan.databinding.ActivityRegisterBinding
import com.zetta.takumiscan.model.DataUser
import com.zetta.takumiscan.util.CacheController
import com.zetta.takumiscan.util.ImagePickerHelper

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private var photo: ByteArray? = null
    private lateinit var imagePickerHelper: ImagePickerHelper
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        dbHelper = DBHelper(this)
        imagePickerHelper = ImagePickerHelper(
            activity = this,
            onSelected = { bitmap, byteArray ->
                photo = byteArray
                binding.profileImage.setImageBitmap(bitmap)
            }
        )

        requestCameraPermission()

        binding.btnRegister.setOnClickListener {
            if (binding.tbNISN.text.isEmpty() || binding.tbNama.text.isEmpty() || binding.tbKelas.text.isEmpty() || binding.tbJurusan.text.isEmpty() || binding.tbPassword.text.isEmpty()){
                Toast.makeText(this, "Semua data harus terisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (photo == null){
                Toast.makeText(this, "Tambahkan foto untuk identitasmu!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            showDialog {
                val data = DataUser(
                    nisn = binding.tbNISN.text.toString(),
                    nama = binding.tbNama.text.toString(),
                    kelas = binding.tbKelas.text.toString(),
                    jurusan = binding.tbJurusan.text.toString(),
                    password = binding.tbPassword.text.toString(),
                    photo = photo!!
                )
                dbHelper.registerUser(data)
                CacheController(this).setStatus("Registered")
                Intent(this, LoginActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
            }
        }

        binding.cardProfile.setOnClickListener {
            selectImage()
        }

        binding.cameraIcon.setOnClickListener {
            selectImage()
        }
    }

    private fun selectImage(){
        imagePickerHelper.showImagePickerDialog()
    }

    private fun requestCameraPermission(){
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission())
        {isGranted:Boolean ->
            if (!isGranted){
                Toast.makeText(this, "Izin kamera diperlukan!", Toast.LENGTH_SHORT).show()
                binding.cameraIcon.isEnabled = false
                binding.profileImage.isEnabled = false
            }
        }
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun showDialog(onSuccess: ()-> Unit){
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi")
            .setMessage("Pembuatan akun hanya akan dilakukan sekali, kamu tidak akan bisa mengubahnya di kemudian hari.\n\nApa kamu yakin semua data telah terisi dengan benar?")
            .setPositiveButton("Yakin"){_, _->
                onSuccess()
            }
            .setNegativeButton("Tidak"){dialog, _ ->
                dialog.dismiss()
            }.show()
    }
}