package com.zetta.takumiscan.presentation.main.profile

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.zetta.takumiscan.data.local.DBHelper
import com.zetta.takumiscan.databinding.ActivityProfileBinding
import com.zetta.takumiscan.util.ImagePickerHelper

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var dbHelper: DBHelper
    private lateinit var imagePickerHelper: ImagePickerHelper

    private var isCameraGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        dbHelper = DBHelper(this)
        imagePickerHelper = ImagePickerHelper(
            activity = this,
            onSelected = { bitmap, byteArray ->
                binding.imgProfile.setImageBitmap(bitmap)
                dbHelper.editPhotoProfileUser(byteArray)
                Toast.makeText(this, "Foto profile berhasil diperbarui", Toast.LENGTH_SHORT).show()
            }
        )
        enableEdgeToEdge()
        setContentView(binding.root)

        imagePickerHelper.requestCameraPermission { isCameraGranted = it }

        initListener()
        overloadData()
    }

    private fun overloadData() {
        val dataUser = dbHelper.getDataUser()
        val profile = BitmapFactory.decodeByteArray(dataUser.photo, 0, dataUser.photo.size)

        val attendedStatus = dbHelper.getStatus()
        val notes = dbHelper.getListNotes()

        val attendedCount = attendedStatus[0] + attendedStatus[1]
        val noteCount = notes.size

        binding.imgProfile.setImageBitmap(profile)
        binding.lblName.text = dataUser.nama
        binding.lblJurusanKelas.text = "Siswa SMKN 24 Jakarta\n${dataUser.jurusan}\n${dataUser.kelas}"
        binding.lblKehadiran.text = attendedCount.toString()
        binding.lblCatatan.text = noteCount.toString()
    }

    private fun initListener() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnChangeProfile.setOnClickListener {
            imagePickerHelper.showImagePickerDialog(isCameraGranted)
        }
    }
}