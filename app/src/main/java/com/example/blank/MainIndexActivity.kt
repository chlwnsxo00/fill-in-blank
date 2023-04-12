package com.example.blank

import Items
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.persona.data.itemAdapter

class MainIndexActivity : AppCompatActivity() {
    private val itemList = ArrayList<Items>()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.topAddListItem -> {
                val input = EditText(this)
                AlertDialog.Builder(this)
                    .setTitle("새로운 목록 추가하기")
                    .setMessage("새로운 목록의 이름을 적어주세요")
                    .setView(input)
                    .setPositiveButton("생성하기") { _, _ ->
                        itemList.add(Items(input.text.toString()))
                        runOnUiThread(kotlinx.coroutines.Runnable {
                            findViewById<RecyclerView>(R.id.rv_item).layoutManager =
                                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                            findViewById<RecyclerView>(R.id.rv_item).adapter = itemAdapter(itemList)
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