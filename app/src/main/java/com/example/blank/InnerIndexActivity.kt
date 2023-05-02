package com.example.blank

import MainIndexItemAdapter
import MainIndexItems
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.IOException


class InnerIndexActivity : AppCompatActivity() {

    private val REQUEST_TAKE_PHOTO = 0
    private val REQUEST_SELECT_IMAGE_IN_ALBUM = 1

    // The URI of photo taken from gallery
    private var mUriPhotoTaken: Uri? = null

    // File of the photo taken with camera
    private var mFilePhotoTaken: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inner_index)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu_main_indxe, menu)
        //R은 res 폴더의 약자. res폴더 안에 있는 context_menu_main.xml 파일과 연결시킨다.
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.takePhoto -> {
                when{
                    ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED ->{
                        Toast.makeText(this,"권한 획득",Toast.LENGTH_SHORT).show()
                        takePhoto()
                    }
                    shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA) ->{
                        Toast.makeText(this,"권한 거부",Toast.LENGTH_SHORT).show()
                        showPermissionContextPopup()
                    }
                    else -> {
                        Toast.makeText(this,"권한요청",Toast.LENGTH_SHORT).show()
                        ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.CAMERA), 1000)
                    }
                }
                true
            }
            R.id.openGallery -> {
                selectImageInAlbum()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showPermissionContextPopup() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("사진을 촬영하기 위해 권한이 필요합니다.")
            .setPositiveButton("동의하기") { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 1000)
            }
            .setNegativeButton("취소하기") { _, _ -> }
            .create()
            .show()
    }


    // Save the activity state when it's going to stop.
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("ImageUri", mUriPhotoTaken)
    }

    // Recover the saved state when the activity is recreated.
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mUriPhotoTaken = savedInstanceState.getParcelable("ImageUri")
    }

    // Deal with the result of selection of the photos and faces.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_TAKE_PHOTO -> if (resultCode == RESULT_OK) {
                val intent = Intent()
                intent.data = Uri.fromFile(mFilePhotoTaken)
                setResult(RESULT_OK, intent)
                finish()
            }
            REQUEST_SELECT_IMAGE_IN_ALBUM -> if (resultCode == RESULT_OK) {
                val imageUri: Uri?
                imageUri = if (data == null || data.data == null) {
                    mUriPhotoTaken
                } else {
                    data.data
                }
                val intent = Intent()
                intent.data = imageUri
                setResult(RESULT_OK, intent)
                finish()
            }
            else -> {}
        }
    }

    // When the button of "Take a Photo with Camera" is pressed.
    private fun takePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            // Save the photo taken to a temporary file.
            val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            try {
                mFilePhotoTaken = File.createTempFile(
                    "IMG_",  /* prefix */
                    ".jpg",  /* suffix */
                    storageDir /* directory */
                )

                // Create the File where the photo should go
                // Continue only if the File was successfully created
                if (mFilePhotoTaken != null) {
                    mUriPhotoTaken = FileProvider.getUriForFile(
                        this,
                        "com.microsoft.projectoxford.visionsample.fileprovider",
                        mFilePhotoTaken!!
                    )
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mUriPhotoTaken)

                    // Finally start camera activity
                    startActivityForResult(intent, REQUEST_TAKE_PHOTO)
                }
            } catch (e: IOException) {
                Log.d("Error",e.stackTraceToString())
            }
        }
    }

    // When the button of "Select a Photo in Album" is pressed.
    private fun selectImageInAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "mFilePhotoTaken/*"
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
        }
    }

}