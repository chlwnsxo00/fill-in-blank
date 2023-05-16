package com.example.blank

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class TextHighlightActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_highlight)

        val intent = getIntent()
        val OCRtext = intent.getStringExtra("text")
        // OCRtext에 OCR한 text가 저장
    }
}