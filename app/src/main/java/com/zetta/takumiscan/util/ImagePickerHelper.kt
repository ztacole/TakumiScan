package com.zetta.takumiscan.util

import android.graphics.Bitmap
import android.provider.MediaStore
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
                    0 -> openCamera()
                    1 -> openGallery()
                    2 -> dialog.dismiss()
                }
            }
            .show()
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