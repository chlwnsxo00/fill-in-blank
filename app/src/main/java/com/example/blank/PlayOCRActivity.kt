package com.example.blank

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.example.ocr.TextRecognition
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestListener

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

    private var ocrJob: Job? = null // 현재 실행 중인 OCR 작업을 추적하기 위한 변수
    private val ocrScope = CoroutineScope(Dispatchers.Default + Job())
    private var fileAbsolutePath: String? = null
    lateinit var takePhotoLauncher: ActivityResultLauncher<Intent>
    private var photoUri: Uri? = null

//이미지의 확장자를 추출하는 메서드
fun getExtension(fileStr: String): String {
    val fileExtension = fileStr.substring(fileStr.lastIndexOf(".") + 1, fileStr.length);
    return fileExtension
}

//갤러리에 찍은 사진을 저장하는 메서드
fun saveImageFile(filename: String, mimeType: String, bitmap: Bitmap): Uri? {
    //이미지 Uri 생성
    //contentValues는 ContentResolver가 사용하는 데이터 정보이다.
    var values = ContentValues()
    //contentValues의 이름, 타입을 정한다.
    values.put(MediaStore.Images.Media.DISPLAY_NAME, filename)
    values.put(MediaStore.Images.Media.MIME_TYPE, mimeType)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // 파일 저장을 완료하기 전까지 다른 곳에서 해당 데이터를 요청하는 것을 무시
        values.put(MediaStore.Images.Media.IS_PENDING, 1)
    }

    // MediaStore에 파일 등록
    val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    try {
        if (uri != null) {
            // 파일 디스크립터 획득
            val descriptor = contentResolver.openFileDescriptor(uri, "w")
            if (descriptor != null) {
                // FileOutputStream으로 비트맵 파일 저장. 숫자는 압축률
                val fos = FileOutputStream(descriptor.fileDescriptor)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.close()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // 데이터 요청 무시 해제
                    values.clear()
                    values.put(MediaStore.Images.Media.IS_PENDING, 0)
                    contentResolver.update(uri, values, null, null)
                }
            }
        }
    } catch (e: java.lang.Exception) {
        Log.e("File", "error=")
    }
    return uri
}
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_play_ocr)

    TextRecognition.initialize(BuildConfig.OCR_API_KEY)
    initMakeProblemButton()

    takePhotoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val photoUri = when (data) {
                    null -> {
                        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                        val photoFile = File(storageDir, "JPEG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.jpg")
                        fileAbsolutePath = photoFile.absolutePath
                        val file = File(fileAbsolutePath)
                        var bitmap: Bitmap? = null
                        //SDK 28버전 미만인 경우 getBitMap 사용
                        if (Build.VERSION.SDK_INT < 28) {
                            //카메라에서 찍은 사진을 비트맵으로 변환
                            bitmap = MediaStore.Images.Media
                                .getBitmap(contentResolver, Uri.fromFile(file))
                            //이미지뷰에 이미지 로딩
                            selectedImage.setImageBitmap(bitmap)
                        } else {
                            //SDK 28버전 이상인 경우 setImageBitmap 사용
                            //카메라에서 찍은 사진을 디코딩
                            val decode = ImageDecoder.createSource(
                                this.contentResolver,
                                Uri.fromFile(file.absoluteFile)
                            )
                            //디코딩한  사진을 비트맵으로 변환
                            bitmap = ImageDecoder.decodeBitmap(decode)
                            //이미지뷰에 이미지 로딩
                            selectedImage.setImageBitmap(bitmap)
                            //갤러리에 저장
                        }

                        if (bitmap != null) {
                            saveImageFile(file.name, getExtension(file.name), bitmap)
                        }else null
                    }
                    else -> {
                        Log.d("data","data")
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
            takePhoto()
            true
        }
        R.id.open_gallery -> {
            openGallery()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}

private fun takePhoto() {
    when {
        ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED -> {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val photoFile: File = getPhotoFile()

            photoFile.also {
                val photoUri: Uri = FileProvider.getUriForFile(
                    this,
                    "com.example.fileprovider",
                    it
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                takePhotoLauncher.launch(takePictureIntent)
            }
        }
        shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA) -> {
            showPermissionContextPopup()
        }
        else -> {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                1000
            )
        }
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
    this@PlayOCRActivity.runOnUiThread {
        OCRtext.text = "                    ------------이미지 리사이징 중------------"
    }

    uri?.let {
        try {
            val requestBuilder = Glide.with(this@PlayOCRActivity)
                .asBitmap()
                .load(it)
                .format(DecodeFormat.PREFER_RGB_565)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .override(1024, 1024) // Desired size

            val originalBitmap: Bitmap = requestBuilder
                .listener(object : RequestListener<Bitmap> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Bitmap>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e("Glide", "Image load failed", e)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Bitmap?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Bitmap>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }
                })
                .submit()
                .get()

            originalBitmap?.let {
                val resizedFile = File(cacheDir, "resized_image.jpg")
                val outputStream = FileOutputStream(resizedFile)
                it.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.flush()
                outputStream.close()
                return@withContext resizedFile
            }
        } catch (e: Exception) {
            throw RuntimeException("Failed to load the image.", e)
        }
    } ?: run {
        throw RuntimeException("The provided Uri is null.")
    }
}


private fun handleOCRFailure(e: Throwable) {
    OCRtext.text = e.toString()
    Log.e("E", e.stackTraceToString())
    // Handle the OCR failure, log or display an error message.
}

private fun showPermissionContextPopup() {
    android.app.AlertDialog.Builder(this)
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