package com.example.blank

import MainIndexItemAdapter
import com.example.data.db.dao.AppDatabase
import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.data.db.entity.MainIndexEntity
import kotlinx.coroutines.*

class MainIndexActivity : AppCompatActivity() {
    private var itemList = ArrayList<MainIndexEntity>()
    private lateinit var db: AppDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MainIndexItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setTitle("목록")
        setContentView(R.layout.activity_main_index)

        // view 초기화
        recyclerView = findViewById(R.id.rv_item)
        // roomDB 초기화
        db = AppDatabase.getInstance(this)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        CoroutineScope(Dispatchers.Main).launch {
            // 기존 DB에 저장되어 있는 항목 불러오기
            itemList = withContext(Dispatchers.IO) {
                db.mainIndexDao().getAllIndexes() as ArrayList<MainIndexEntity>
            }
            // recyclerview adapter 초기화
            adapter = MainIndexItemAdapter(db, itemList)
            recyclerView.adapter = adapter
        }
    }
    // 이 액티비티와 top_menu_main_index를 연결
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu_main_indxe, menu)
        //R은 res 폴더의 약자. res폴더 안에 있는 context_menu_main.xml 파일과 연결시킨다.
        return super.onCreateOptionsMenu(menu)
    }

    // 상단 메뉴 item 선택시 이벤트
    @OptIn(DelicateCoroutinesApi::class)
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
                    .setPositiveButton("생성하기")  { _, _ ->
                        //itemList.add(MainIndexItems(input.text.toString(),text_start_Inner_Index))
                        GlobalScope.launch(Dispatchers.IO) {
                            // item insert 쿼리 수행
                            insertItem(input.text.toString(), text_start_Inner_Index)
                            withContext(Dispatchers.Main) {
                                // insert 작업이 끝나면 해당 함수 실행
                                getAllItem()
                            }
                        }
                    }
                    .setNegativeButton("취소하기") { _, _ -> }
                    .create()
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    suspend fun insertItem(title: String, innerIndex: String) {
        // 가장 큰 id 값 조회
        val maxId = db.mainIndexDao().getMaxId() ?: 1

        // 새로운 id 값 설정
        val newId = maxId + 1

        // insert 쿼리 수행
        db.mainIndexDao().insert(
            MainIndexEntity(
                id = newId.toLong(),
                itemTitle = title,
                text_start_Inner_Index = innerIndex
            )
        )
    }

    suspend fun getAllItem() {
        CoroutineScope(Dispatchers.Main).launch {
            // 모든 mainindex 아이템 가져오기
            val indexes = withContext(Dispatchers.IO) {
                db.mainIndexDao().getAllIndexes() as ArrayList<MainIndexEntity>
            }

            // recyclerview 아이템 업데이트
            adapter.setItems(indexes)
        }
    }
}