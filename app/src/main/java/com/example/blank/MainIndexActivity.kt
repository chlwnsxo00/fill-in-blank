package com.example.blank

import android.os.Bundle
import android.view.ContextMenu
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainIndexActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setTitle("목록")
        setContentView(R.layout.activity_main_index)


    }
    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        val inflater = menuInflater
        menuInflater.inflate(R.menu.top_menu_main_indxe, menu)
        //R은 res 폴더의 약자. res폴더 안에 있는 context_menu_main.xml 파일과 연결시킨다.
    }
}