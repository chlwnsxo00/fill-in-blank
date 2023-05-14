package com.example.blank

import MainIndexItemAdapter
import MainIndexItems
import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainIndexActivity : AppCompatActivity() {
    private val itemList = ArrayList<MainIndexItems>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setTitle("목록")
        setContentView(R.layout.activity_main_index)

    // 범수님 여기(MainIndexActivity)랑 InnerActivity에 Room이 구현되어야 하는데,
    // 여기에서 item을 클릭해 다음 액티비티로 넘어갈때 해당 item에 맞는 문제들이 보여져야 함
    // ex) 영어문제목록을 클릭하면 영어문제들이 나와야함
    // InnerActivity에서 Room을 구현해 문제를 만들고 저장하면 추가되고 database 저장되어야 함.

    }
    // 이 액티비티와 top_menu_main_index를 연결
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu_main_indxe, menu)
        //R은 res 폴더의 약자. res폴더 안에 있는 context_menu_main.xml 파일과 연결시킨다.
        return super.onCreateOptionsMenu(menu)
    }

    // 상단 메뉴 item 선택시 이벤트
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.topAddListItem -> {
                val input = EditText(this)

                val text_start_Inner_Index = "지문 0개 >"
                // TODO 이 부분 나중에 지문 개수를 받아서 지문 개수 출력하는 걸로 수정해야함


                AlertDialog.Builder(this)
                    .setTitle("새로운 목록 추가하기")
                    .setMessage("새로운 목록의 이름을 적어주세요")
                    .setView(input)
                    .setPositiveButton("생성하기") { _, _ ->
                        itemList.add(MainIndexItems(input.text.toString(),text_start_Inner_Index))
                        runOnUiThread(kotlinx.coroutines.Runnable {
                            findViewById<RecyclerView>(R.id.rv_item).layoutManager =
                                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                            findViewById<RecyclerView>(R.id.rv_item).adapter = MainIndexItemAdapter(itemList)
                        })
                    }
                    .setNegativeButton("취소하기") { _, _ -> }
                    .create()
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}