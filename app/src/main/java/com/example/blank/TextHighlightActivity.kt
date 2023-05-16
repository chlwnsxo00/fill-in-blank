package com.example.blank

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.widget.TextView

class TextHighlightActivity : AppCompatActivity() {

    private val textView: TextView by lazy {
        findViewById(R.id.textView3)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_highlight)


//        val textView: TextView = findViewById(R.id.textView3)
        val intent = intent
        val ocrText = intent.getStringExtra("text")

//        textView.text = ocrText
        textView.text = "Hello World!"

        textView.setOnLongClickListener {
            val start = textView.selectionStart
            val end = textView.selectionEnd

            if (start >= 0 && end >= 0 && start != end) {
                val text = textView.text
                val builder = StringBuilder(text)
                builder.replace(start, end, "")
                textView.text = builder.toString()

                // 텍스트 하이라이트 설정
                val spannableString = SpannableString(textView.text)
                spannableString.setSpan(
                    BackgroundColorSpan(Color.YELLOW),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                textView.text = spannableString
                true
            } else {
                false
            }
        }
    }
}

//    private fun initMakeProblemButton() {
//        makeProblem.setOnClickListener {
//            intent = Intent(this, TextHighlightActivity::class.java)
//            intent.putExtra("text",OCRtext.text)
//            startActivity(intent)
//        }
//    }


