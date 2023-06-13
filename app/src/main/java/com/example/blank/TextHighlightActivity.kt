package com.example.blank

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import java.util.*

class TextHighlightActivity : AppCompatActivity() {

    private val textView: TextView by lazy {
        findViewById(R.id.textView)
    }
    private val makeButton: AppCompatButton by lazy {
        findViewById(R.id.btn_save_Blank)
    }

    private val answerWordQueue: Queue<String> = LinkedList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_highlight)
//        val intent = intent
//        val ocrText = intent.getStringExtra("text")
//
//        textView.text = ocrText

        textView.text = "The sun rises in the east and sets in the west."

        initTextView()
        initMakeProblemButton()
    }

    private fun initTextView() {
        textView.movementMethod = CustomLinkMovementMethod() // CustomLinkMovementMethod 설정

        val text = textView.text.toString()
        val words = text.split(" ")

        val spannableString = SpannableString(text)
        for (word in words) {
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(view: View) {
                    answerWordQueue.add(word) // 클릭한 단어를 answerWordQueue에 추가
                    replaceWordWithUnderscore(textView, word) // 단어를 _로 변경
                }
            }

            val wordStartIndex = text.indexOf(word)
            val wordEndIndex = wordStartIndex + word.length
            spannableString.setSpan(clickableSpan, wordStartIndex, wordEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        textView.text = spannableString
    }

    // 선택된 단어를 단어의 알파벳 수만큼의 "_" 로 바꾸는 함수
    private fun replaceWordWithUnderscore(textView: TextView, word: String) {
        val text = textView.text.toString()
        val underscoreWord = "_".repeat(word.length)
        val replacedText = text.replace(word, underscoreWord)
        textView.text = replacedText
    }

    // Custom MovementMethod 클래스
    inner class CustomLinkMovementMethod : LinkMovementMethod() {
        override fun onTouchEvent(widget: TextView, buffer: Spannable, event: MotionEvent): Boolean {
            val action = event.action

            if (action == MotionEvent.ACTION_UP) {
                val x = event.x.toInt()
                val y = event.y.toInt()

                val layout = widget.layout
                val line = layout.getLineForVertical(y)
                val off = layout.getOffsetForHorizontal(line, x.toFloat())

                val link = buffer.getSpans(off, off, ClickableSpan::class.java)
                if (link.isNotEmpty()) {
                    link[0].onClick(widget)
                    return true
                }
            }
            return super.onTouchEvent(widget, buffer, event)
        }
    }
    private fun initMakeProblemButton() {
        makeButton.setOnClickListener {
            intent = Intent(this, InnerIndexActivity::class.java)
            intent.putExtra("text",textView.text)
            startActivity(intent)
        }
    }
}






