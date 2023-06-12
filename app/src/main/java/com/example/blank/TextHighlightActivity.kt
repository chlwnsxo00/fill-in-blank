package com.example.blank

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TextHighlightActivity : AppCompatActivity() {

    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_highlight)

        textView = findViewById(R.id.textView)
        val intent = intent
        val ocrText = intent.getStringExtra("text")

        textView.text = ocrText
        println(ocrText)
        //     textView.text = "Hello World!"

        textView.setOnLongClickListener {
            textView.requestFocus()
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
                println(start)
                true
            } else {
                false
            }
        }

        initMakeProblemButton()
    }

    private fun initMakeProblemButton() {
        val makeProblemButton: Button = findViewById(R.id.makeProblemButton)
        makeProblemButton.setOnClickListener {
            val intent = Intent(this, TextHighlightActivity::class.java)
            intent.putExtra("text", textView.text.toString())
            startActivity(intent)
        }
    }
}