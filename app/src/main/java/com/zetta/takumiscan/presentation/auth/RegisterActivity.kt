package com.zetta.takumiscan.presentation.auth

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.zetta.takumiscan.R
import com.zetta.takumiscan.data.local.DBHelper
import com.zetta.takumiscan.databinding.ActivityRegisterBinding
import com.zetta.takumiscan.model.DataUser
import com.zetta.takumiscan.util.CacheController
import com.zetta.takumiscan.util.ImagePickerHelper
import com.zetta.takumiscan.util.core.CoreFunction.showDialog

private val listJurusan = listOf(
    "Rekayasa Perangkat Lunak",
    "Perhotelan",
    "Kuliner",
    "Usaha Layanan Wisata",
    "Tata Busana"
)

private val listKelas = listOf(
    listOf(
        "X RPL 1",
        "X RPL 2",
        "XI RPL 1",
        "XI RPL 2",
        "XII RPL 1",
        "XII RPL 2",
    ),
    listOf(
        "X PH 1",
        "X PH 2",
        "X PH 3",
        "XI PH 1",
        "XI PH 2",
        "XI PH 3",
        "XII PH 1",
        "XII PH 2",
        "XII PH 3",
    ),
    listOf(
        "X KUL 1",
        "X KUL 2",
        "X KUL 3",
        "XI KUL 1",
        "XI KUL 2",
        "XI KUL 3",
        "XII KUL 1",
        "XII KUL 2",
        "XII KUL 3",
    ),
    listOf(
        "X ULW",
        "XI ULW",
        "XII ULW",
    ),
    listOf(
        "X TBS 1",
        "X TBS 2",
        "X TBS 3",
        "XI TBS 1",
        "XI TBS 2",
        "XI TBS 3",
        "XII TBS 1",
        "XII TBS 2",
        "XII TBS 3",
    )
)

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var dbHelper: DBHelper

    private var photo: ByteArray? = null

    private lateinit var imagePickerHelper: ImagePickerHelper
    private var isCameraGranted = false

    private var isPasswordVisible = false

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
                binding.imgProfile.setImageBitmap(bitmap)
            }
        )

        showDialog(
            title = "Perhatian!",
            message = "Data yang kamu isi disini tidak akan dibagikan kemanapun, harap isi data dengan informasi yang sesuai",
            positiveButtonText = "OK",
            cancellable = false,
            onPositiveButtonClick = { dialog, _ ->
                dialog.dismiss()
            }
        )

        requestCameraPermission()

        initJurusanDropdown()
        initListener()
    }

    private fun initJurusanDropdown() {
        val adapter = ArrayAdapter(this, R.layout.textview_dropdown, listJurusan)
        binding.tbJurusan.setAdapter(adapter)
    }

    private fun initKelasDropdown(index: Int){
        val adapter = ArrayAdapter(this, R.layout.textview_dropdown, listKelas[index])
        binding.tbKelas.setAdapter(adapter)
    }

    private fun initListener(){
        binding.btnRegister.setOnClickListener {
            registerProcess()
        }

        binding.cardProfile.setOnClickListener {
            selectImage()
        }

        binding.cameraIcon.setOnClickListener {
            selectImage()
        }

        binding.tbJurusan.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            binding.tbKelas.isEnabled = true

            initKelasDropdown(position)
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

    private fun registerProcess() {
        if (binding.tbNISN.text.isEmpty() || binding.tbNama.text.isEmpty() || binding.tbKelas.text.isEmpty() || binding.tbJurusan.text.isEmpty() || binding.tbPassword.text.isEmpty()){
            Toast.makeText(this, "Semua data harus terisi!", Toast.LENGTH_SHORT).show()
            return
        }
        if (photo == null){
            Toast.makeText(this, "Tambahkan foto untuk identitasmu!", Toast.LENGTH_SHORT).show()
            return
        }
        showDialog(
            title = "Konfirmasi",
            message = "Pembuatan akun hanya akan dilakukan sekali, kamu tidak akan bisa mengubahnya di kemudian hari.\n\nApa kamu yakin semua data telah terisi dengan benar?",
            positiveButtonText = "Yakin",
            onPositiveButtonClick = DialogInterface.OnClickListener { dialog, _ ->
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
            },
            negativeButtonText = "Tidak",
            onNegativeButtonClick = DialogInterface.OnClickListener { dialog, _ ->
                dialog.dismiss()
            }
        )
    }

    private fun selectImage(){
        imagePickerHelper.showImagePickerDialog(isCameraGranted)
    }

    private fun requestCameraPermission(){
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission())
        {isGranted:Boolean ->
            isCameraGranted = isGranted
        }
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }
}