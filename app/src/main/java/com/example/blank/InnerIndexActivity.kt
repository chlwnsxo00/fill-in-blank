package com.example.blank

import InnerIndexItemAdapter
import MainIndexItemAdapter
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.data.db.dao.AppDatabase
import com.example.data.db.entity.InnerIndexEntity
import com.example.data.db.entity.MainIndexEntity


class InnerIndexActivity : AppCompatActivity() {

    private var itemList = ArrayList<InnerIndexEntity>()
    private lateinit var db: AppDatabase
    private lateinit var adapter: InnerIndexItemAdapter
    private val rv : RecyclerView by lazy {
        findViewById(R.id.inner_index_rv)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inner_index)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu_inner_index, menu)
        //R은 res 폴더의 약자. res폴더 안에 있는 context_menu_main.xml 파일과 연결시킨다.
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.addProblem -> {
                // 상단 메뉴 + 아이콘 클릭
                val intent = Intent(this, PlayOCRActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    //이후 문제 누르면 문제 푸는 Activity로 넘어감


}