package com.zetta.takumiscan.util

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayOutputStream

class ImagePickerHelper(private val activity: AppCompatActivity, private val onSelected: (Bitmap, ByteArray)-> Unit) {

    private val cameraLauncher = activity.registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ){ bitmap ->
        bitmap?.let {
            onImageSelected(it)
        }
    }

    private val galleryLauncher = activity.registerForActivityResult(
        ActivityResultContracts.GetContent()
    ){ uri ->
        uri?.let {
            val bitmap = MediaStore.Images.Media.getBitmap(
                activity.contentResolver,
                uri
            )
            onImageSelected(bitmap)
        }
    }

    fun showImagePickerDialog() {
        val options = arrayOf("Ambil Foto", "Pilih dari Galeri", "Batal")

        androidx.appcompat.app.AlertDialog.Builder(activity)
            .setTitle("Pilih Sumber Gambar")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> requestCameraPermission()
                    1 -> openGallery()
                    2 -> dialog.dismiss()
                }
            }
            .show()
    }

    private fun requestCameraPermission(){
        val requestPermissionLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission())
        {isGranted:Boolean ->
            if(isGranted){
                openCamera()
            }else{
                Toast.makeText(activity, "Izin kamera diperlukan!", Toast.LENGTH_SHORT).show()
            }
        }
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    fun openCamera(){
        cameraLauncher.launch(null)
    }

    private fun openGallery(){
        galleryLauncher.launch("image/*")
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        return outputStream.toByteArray()
    }

    private fun onImageSelected(bitmap: Bitmap) {
        val byteArray = bitmapToByteArray(bitmap)
        onSelected(bitmap, byteArray)
    }
}