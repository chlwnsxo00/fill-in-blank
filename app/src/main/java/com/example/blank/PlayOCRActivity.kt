package com.example.blank

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.ocr.TextRecognition
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class PlayOCRActivity : AppCompatActivity() {

    private val selectedImage: ImageView by lazy {
        findViewById(R.id.selectedImage)
    }

    private val makeProblem: AppCompatButton by lazy {
        findViewById(R.id.makeProblem)
    }

    private val OCRtext: TextView by lazy {
        findViewById(R.id.OCRtext)
    }
    val REQUEST_TAKE_PHOTO = 1
    var mCurrentPhotoPath: String? = null
    private var ocrJob: Job? = null // 현재 실행 중인 OCR 작업을 추적하기 위한 변수
    private val ocrScope = CoroutineScope(Dispatchers.Default + Job())

    private val takePhotoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val photoUri = when (data) {
                    null -> {
                        val photoFile = getPhotoFile()
                        FileProvider.getUriForFile(this, "com.example.fileprovider", photoFile)
                    }
                    else -> {
                        data.data
                    }
                }

                try {
                    ocrJob?.cancel()

                    ocrJob = ocrScope.launch {
                        val file = resizeImage(photoUri)
                        val image = selectedImage
                        launch(Dispatchers.Main) {
                            OCRtext.text = "                  --------분석 중----------"
                            image.setImageURI(photoUri)
                            image.scaleType = ImageView.ScaleType.CENTER_CROP
                        }
                        if (!isActive) return@launch
                        TextRecognition.compressFile(this@PlayOCRActivity, file) { compressedFile ->
                            TextRecognition.fromFile(
                                compressedFile,
                                object : TextRecognition.Callback {
                                    override fun success(response: String) {
                                        OCRtext.text = response
                                    }

                                    override fun failure(e: Throwable) {
                                        handleOCRFailure(e)
                                    }
                                })
                        }
                    }
                } catch (e: Exception) {
                    handleOCRFailure(e)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_ocr)

        TextRecognition.initialize(BuildConfig.OCR_API_KEY)
        initMakeProblemButton()
    }

    private fun initMakeProblemButton() {
        makeProblem.setOnClickListener {
            intent = Intent(this, TextHighlightActivity::class.java)
            intent.putExtra("text", OCRtext.text)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu_play_ocr_index, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.take_photo -> {
                dispatchTakePictureIntent()
                true
            }
            R.id.open_gallery -> {
                openGallery()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // 사진 촬영 후 썸네일만 띄워줌. 이미지를 파일로 저장해야 함
    @Throws(IOException::class)
    private fun createImageFile(): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )
        mCurrentPhotoPath = image.absolutePath
        return image
    }

    // 카메라 인텐트 실행하는 부분
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
            }
            if (photoFile != null) {
                val photoURI =
                    FileProvider.getUriForFile(this, "com.example.fileprovider", photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        try {
            when (requestCode) {
                REQUEST_TAKE_PHOTO -> {
                    if (resultCode == RESULT_OK) {
                        val file = File(mCurrentPhotoPath)
                        val bitmap: Bitmap?
                        if (Build.VERSION.SDK_INT >= 29) {
                            val source: ImageDecoder.Source = ImageDecoder.createSource(
                                contentResolver, Uri.fromFile(file)
                            )
                            try {
                                bitmap = ImageDecoder.decodeBitmap(source)
                                if (bitmap != null) {
                                    selectedImage.setImageBitmap(bitmap)
                                }

                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        } else {
                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(
                                    contentResolver,
                                    Uri.fromFile(file)
                                )
                                if (bitmap != null) {
                                    selectedImage.setImageBitmap(bitmap)
                                }
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        } catch (error: java.lang.Exception) {
            error.printStackTrace()
        }
    }

    private fun openGallery() {
        Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }.also {
            takePhotoLauncher.launch(it)
        }
    }

    private fun getPhotoFile(): File {
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}_",
            ".jpg",
            storageDir
        )
    }

    private suspend fun resizeImage(uri: Uri?): File = withContext(Dispatchers.IO) {
        runOnUiThread {
            OCRtext.text = "--------이미지 리사이징 중--------"
        }

        uri?.let {
            val requestBuilder = Glide.with(this@PlayOCRActivity)
                .asBitmap()
                .load(it)
                .format(DecodeFormat.PREFER_RGB_565)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .override(1024, 1024) // Desired size
                .submit()

            try {
                val originalBitmap: Bitmap = requestBuilder.get()

                originalBitmap?.let {
                    val resizedFile = File(cacheDir, "resized_image.jpg")
                    val outputStream = FileOutputStream(resizedFile)
                    it.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    outputStream.flush()
                    outputStream.close()
                    return@withContext resizedFile
                }
            } catch (e: Exception) {
                handleResizeImageException(e)
            }
        }?: run {
            // uri가 null인 경우 기본 공백 이미지를 처리하도록 수정합니다.
            val bmp = Bitmap.createBitmap(1024, 1024, Bitmap.Config.RGB_565)
            val resizedFile = File(cacheDir, "default_image.jpg")
            val outputStream = FileOutputStream(resizedFile)
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            return@withContext resizedFile
        }

        throw RuntimeException("The provided Uri is null.")
    }


    private fun handleResizeImageException(e: Exception) {
        e.printStackTrace()
        Log.e("E", e.stackTraceToString())
        runOnUiThread {
            Toast.makeText(this@PlayOCRActivity, "Failed to resize the image.", Toast.LENGTH_SHORT)
                .show()
        }
    }


    private fun handleOCRFailure(e: Throwable) {
        OCRtext.text = e.toString()
        Log.e("E", e.stackTraceToString())
        // Handle the OCR failure, log or display an error message.
    }
}
