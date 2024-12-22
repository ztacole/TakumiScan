package com.zetta.takumiscan.presentation.main.menu.scan

import android.Manifest
import android.animation.TimeInterpolator
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.util.TypedValue
import android.view.animation.Interpolator
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.zetta.takumiscan.databinding.ActivityScanBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import com.zetta.takumiscan.R
import com.zetta.takumiscan.data.local.DBHelper
import com.zetta.takumiscan.databinding.DialogMoodBinding
import com.zetta.takumiscan.databinding.DialogStoryBinding
import com.zetta.takumiscan.model.DataUser
import com.zetta.takumiscan.model.History
import com.zetta.takumiscan.presentation.main.MainActivity
import com.zetta.takumiscan.util.ImagePickerHelper
import com.zetta.takumiscan.util.core.CoreFunction.showDialog
import java.net.URL

class ScanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScanBinding

    private lateinit var imagePickerHelper: ImagePickerHelper

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var barcodeScanner: BarcodeScanner
    private lateinit var camera: Camera
    private lateinit var cameraControl: CameraControl
    private lateinit var cameraProvider: ProcessCameraProvider
    private var isFlashOn = false

    private lateinit var dbHelper: DBHelper
    private lateinit var dataUser: DataUser

    private var mood: String? = null
    private var photo: ByteArray? = null
    private val BATAS_WAKTU_HADIR = java.util.Calendar.getInstance().apply {
        set(java.util.Calendar.HOUR_OF_DAY, 6)
        set(java.util.Calendar.MINUTE, 30)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityScanBinding.inflate(layoutInflater)
        dbHelper = DBHelper(this)
        dataUser = dbHelper.getDataUser()
        imagePickerHelper = ImagePickerHelper(
            activity = this,
            onSelected = { _, byteArray ->
                photo = byteArray
                showDialogMood()
            }
        )
        setContentView(binding.root)

        cameraExecutor = Executors.newSingleThreadExecutor()
        barcodeScanner = BarcodeScanning.getClient()

        requestCameraPermission()

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnFlash.setOnClickListener {
            if (isFlashOn){
                isFlashOn = false
                binding.btnFlash.setImageDrawable(getDrawable(R.drawable.baseline_flash_off_24))
            }
            else{
                isFlashOn = true
                binding.btnFlash.setImageDrawable(getDrawable(R.drawable.baseline_flash_on_24))
            }
            toggleFlash()
        }
    }

    private fun requestCameraPermission(){
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission())
        {isGranted:Boolean ->
            if(isGranted){
                startCamera()
            }else{
                Toast.makeText(this, "Izin kamera diperlukan!", Toast.LENGTH_SHORT).show()
            }
        }
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun startCamera(){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        val screenSize = Size(1280,720)
        val resolutionSelector = ResolutionSelector.Builder().setResolutionStrategy(
            ResolutionStrategy(screenSize, ResolutionStrategy.FALLBACK_RULE_NONE)
        ).build()

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().setResolutionSelector(resolutionSelector)
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewCam.surfaceProvider)
                }
            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        processImageProxy(imageProxy)
                    }
                }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageAnalyzer
            )
            cameraControl = camera.cameraControl
        }, ContextCompat.getMainExecutor(this))
    }

    private fun stopCamera(){
        if (::cameraProvider.isInitialized) cameraProvider.unbindAll()
    }

    @OptIn(ExperimentalGetImage::class)
    private fun processImageProxy(imageProxy: ImageProxy){
        val mediaImage = imageProxy.image
        if(mediaImage != null){
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            barcodeScanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes){
                        handleBarcode(barcode)
                    }
                }
                .addOnFailureListener{
                    Toast.makeText(this, "Failed to scan QR Code", Toast.LENGTH_SHORT).show()
                    Log.e("Process Image", "processImageProxy: Failed to scan QR Code")
                }
                .addOnCompleteListener{
                    imageProxy.close()
                }
        }
    }

    private fun handleBarcode(barcode: Barcode){
        val url = barcode.url?.url?:barcode.displayValue
        if (url != null){
            val customUrl = URL("https://backend24.com/hello.php?nama=${dataUser.nama}&lokasi=${intent.getStringExtra("lokasi")}")
            Toast.makeText(this, "Scan berhasil", Toast.LENGTH_SHORT).show()
            Log.d("QR URL", "handleBarcode: $url")
            Log.d("Custom URL", "handleBarcode: $customUrl")

            stopCamera()

            showDialog(
                title = "Penting!",
                message = "Silahkan absen wajah kamu",
                positiveButtonText = "Ok",
                onPositiveButtonClick = DialogInterface.OnClickListener { dialog, _ ->
                    dialog.dismiss()
                    imagePickerHelper.openCamera()
                }
            )
        }else{
            Toast.makeText(this, "Failed to scan QR Code", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleFlash(){
        val hasTorch = camera.cameraInfo.hasFlashUnit()

        if (hasTorch){
            cameraControl.enableTorch(isFlashOn)
        }
        else{
            Toast.makeText(this, "Flashlight not available!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun showDialogMood(){
        val moodView = DialogMoodBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(this)
            .setView(moodView.root)
            .setCancelable(false)
            .create()

        dialog.setOnShowListener{
            moodView.cardSad.setOnClickListener {
                mood = "Sedih"
                showDialogStory()
                dialog.dismiss()
            }
            moodView.cardFlat.setOnClickListener {
                mood = "Datar"
                showDialogStory()
                dialog.dismiss()
            }
            moodView.cardSmile.setOnClickListener {
                mood = "Senang"
                showDialogStory()
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun showDialogStory(){
        val storyView = DialogStoryBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(this)
            .setView(storyView.root)
            .setCancelable(false)
            .create()

        dialog.setOnShowListener{
            storyView.btnKirim.setOnClickListener {
                val data = History(
                    id = 0,
                    status = if (Calendar.getInstance().time.before(BATAS_WAKTU_HADIR.time)) "Tepat Waktu" else "Terlambat",
                    photo = photo!!,
                    mood = mood!!,
                    dateTime = Calendar.getInstance().time.toString()
                )

                dbHelper.addHistory(data)
                dialog.dismiss()
                animateOnExit()
            }
        }

        dialog.show()
    }

    private fun animateOnExit(){
        binding.frameAnimation.animate().apply {
            scaleY(100f)
            duration = 300
            withEndAction {
                binding.lblMessage.animate().apply {
                    alpha(1f)
                    duration = 300
                    withEndAction {
                        val translate = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, resources.displayMetrics)

                        binding.iconSuccess.animate().apply {
                            scaleY(1.15f)
                            scaleX(1.15f)
                            translationY(-translate)
                            duration = 500
                            withEndAction {
                                binding.iconSuccess.animate().apply {
                                    scaleY(1f)
                                    scaleX(1f)
                                    translationY(translate)
                                    duration = 300
                                    withEndAction {
                                        binding.iconSuccess.animate().apply {
                                            scaleY(1f)
                                            duration = 600
                                            withEndAction {
                                                finish()
                                                binding.iconSuccess.animate().alpha(0f).setDuration(200)
                                                binding.lblMessage.animate().alpha(0f).setDuration(200)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}