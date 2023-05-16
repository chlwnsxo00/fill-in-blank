package com.example.blank

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.ocr.TextRecognition
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream

class PlayOCRActivity : AppCompatActivity() {

    private val selectedImage: ImageView by lazy {
        findViewById(R.id.selectedImage)
    }

    private val makeProblem: AppCompatButton by lazy{
        findViewById(R.id.makeProblem)
    }

    private val OCRtext: TextView by lazy {
        findViewById(R.id.OCRtext)
    }

    private var ocrJob: Job? = null // 현재 실행 중인 OCR 작업을 추적하기 위한 변수
    private val ocrScope = CoroutineScope(Dispatchers.Default + Job())

    private var pickImageIntent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {

                    val uri: Uri = result.data?.data!!
                    ocrJob?.cancel()

                    ocrJob = ocrScope.launch {
                        val file = resizeImage(uri)
                        val image = selectedImage
                        launch(Dispatchers.Main) {
                            OCRtext.text = "                  --------분석 중----------"
                            image.setImageURI(uri)
                            image.scaleType = ImageView.ScaleType.CENTER_CROP
                        }
                        if (!isActive) return@launch
                        TextRecognition.compressFile(this@PlayOCRActivity, file) {
                            TextRecognition.fromFile(it, object : TextRecognition.Callback {
                                override fun success(response: String) {
                                    OCRtext.text = response
                                }

                                override fun failure(e: Throwable) {
                                    OCRtext.text = e.toString()
                                    Log.e("E", e.stackTraceToString())
                                }
                            })
                        }
                    }
                } catch (e: Exception) {
                    OCRtext.text = e.toString()
                    Log.e("E",e.stackTraceToString())
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_ocr)

        TextRecognition.initialize("K86472781588957")
        initMakeProblemButton()
    }

    private fun initMakeProblemButton() {
        makeProblem.setOnClickListener {
            intent = Intent(this, TextHighlightActivity::class.java)
            intent.putExtra("text",OCRtext.text)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu_play_ocr_index, menu)
        //R은 res 폴더의 약자. res폴더 안에 있는 context_menu_main.xml 파일과 연결시킨다.
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.take_photo -> {
                // 사진찍기
                when {
                    ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        Toast.makeText(this, "권한 획득", Toast.LENGTH_SHORT).show()
                        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                            // Ensure that there's a camera activity to handle the intent
                            takePictureIntent.resolveActivity(packageManager)
                        }.also {
                            pickImageIntent.launch(it)
                        }
                    }
                    shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA) -> {
                        Toast.makeText(this, "권한 거부", Toast.LENGTH_SHORT).show()
                        showPermissionContextPopup()
                    }
                    else -> {
                        Toast.makeText(this, "권한요청", Toast.LENGTH_SHORT).show()
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(android.Manifest.permission.CAMERA),
                            1000
                        )
                    }
                }
                true
            }
            R.id.open_gallery -> {
                // 갤러리 사진고르기
                Intent().apply {
                    type = "image/*"
                    action = Intent.ACTION_GET_CONTENT
                }.also {
                    pickImageIntent.launch(it)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun resizeImage(uri: Uri): File {
        // Glide 이용해 고해상도 사진을 1MB 이하가 되기 위해 해상도를 줄임
        // OCR 무료 버전에서 1MB 이하만 가능하기 때문
        runOnUiThread(kotlinx.coroutines.Runnable {
            OCRtext.text = "                    ------------이미지 리사이징 중------------ "
        })

        val requestBuilder = Glide.with(this)
            .asBitmap()
            .load(uri)
            .format(DecodeFormat.PREFER_RGB_565)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .override(1024) // 원하는 크기로 설정
            .submit()

        val originalBitmap: Bitmap = requestBuilder.get()

        originalBitmap?.let {
            val resizedFile = File(cacheDir, "resized_image.jpg")
            val outputStream = FileOutputStream(resizedFile)
            it.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            return resizedFile
        }
        throw RuntimeException("Failed to resize the image.")
    }

    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("카메라 사용을 위해 권한이 필요합니다.")
            .setPositiveButton("동의하기") { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 1000)
            }
            .setNegativeButton("취소하기") { _, _ -> }
            .create()
            .show()
    }
}
