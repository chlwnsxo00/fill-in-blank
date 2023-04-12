package com.example.blank

import android.app.AlertDialog
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainIndexActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setTitle("목록")
        setContentView(R.layout.activity_main_index)


    }

    // 이 액티비티와 top_menu_main_index를 연결
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu_main_indxe, menu)
        //R은 res 폴더의 약자. res폴더 안에 있는 context_menu_main.xml 파일과 연결시킨다.
        return super.onCreateOptionsMenu(menu)
    }

    // 상단 목록의 + 클릭시 발생하는 이벤트 메소드
    fun addList(){
        val builder = AlertDialog.Builder(this)
        val input = EditText(this)
        builder.setView(input)

        builder.setTitle("제목")
        builder.setPositiveButton("확인") { dialogInterface, i ->
            val userInput = input.text.toString()

            // 입력된 문자열 처리
        }
        builder.setNegativeButton("취소") { dialogInterface, i ->
            // 취소 버튼 클릭 아무런 작동 X
        }
        val dialog = builder.create()
        dialog.show()
    }
}