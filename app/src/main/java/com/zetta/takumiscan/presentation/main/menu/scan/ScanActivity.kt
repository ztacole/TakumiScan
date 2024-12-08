package com.zetta.takumiscan.presentation.main.menu.scan

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
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
import com.zetta.takumiscan.model.DataUser
import java.net.URL

class ScanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScanBinding
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var barcodeScanner: BarcodeScanner
    private var isFlashOn = false
    private lateinit var camera: Camera
    private lateinit var cameraControl: CameraControl
    private lateinit var dbHelper: DBHelper
    private lateinit var dataUser: DataUser

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityScanBinding.inflate(layoutInflater)
        dbHelper = DBHelper(this)
        dataUser = dbHelper.getDataUser()
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
            val cameraProvider = cameraProviderFuture.get()
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
}