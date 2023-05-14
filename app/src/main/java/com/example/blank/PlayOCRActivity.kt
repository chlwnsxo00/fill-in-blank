package com.example.blank

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.ACTION_IMAGE_CAPTURE
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.codingwithmehdi.android.cloud.ocr.TextRecognition
import java.io.File

class PlayOCRActivity : AppCompatActivity() {
    private var pickImageIntent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    val uri: Uri = result.data?.data!!
                    val file = File(getRealPathFromURI(uri))

                    val image = findViewById<ImageView>(R.id.imageView)
                    image.setImageURI(uri)
                    TextRecognition.compressFile(this, file) {
                        TextRecognition.fromFile(it, object : TextRecognition.Callback {
                            override fun success(response: String) {
                                val contentLabel = findViewById<TextView>(R.id.OCRtext)
                                contentLabel.text = response
                            }

                            override fun failure(e: Throwable) {
                                val contentLabel = findViewById<TextView>(R.id.OCRtext)
                                contentLabel.text = e.toString()
                            }

                        })
                    }
                } catch (e: Exception) {
                    val contentLabel = findViewById<TextView>(R.id.OCRtext)
                    contentLabel.text = e.toString()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_ocr)

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
                when{
                    ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED ->{
                        Toast.makeText(this,"권한 획득", Toast.LENGTH_SHORT).show()
                        Intent().apply {
                            type = "image/*"
                            action = MediaStore.ACTION_IMAGE_CAPTURE
                        }.also {
                            pickImageIntent.launch(it)
                        }
                    }
                    shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA) ->{
                        Toast.makeText(this,"권한 거부", Toast.LENGTH_SHORT).show()
                        showPermissionContextPopup()
                    }
                    else -> {
                        Toast.makeText(this,"권한요청", Toast.LENGTH_SHORT).show()
                        ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.CAMERA), 1000)
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

    private fun getRealPathFromURI(contentUri: Uri?): String? {
        var cursor: Cursor? = null
        try {
            cursor = contentResolver.query(contentUri!!, null, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val document = cursor.getString(0)
                val documentId = document.substring(document.lastIndexOf(":") + 1)
                cursor.close()
                cursor = contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null,
                    MediaStore.Images.Media._ID + " = ? ",
                    arrayOf(documentId),
                    null
                )
                if (cursor != null && cursor.moveToFirst()) {
                    val path = cursor.getString(cursor.getColumnIndexOrThrow("_data"))
                    cursor.close()
                    return path
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return null
    }

}
