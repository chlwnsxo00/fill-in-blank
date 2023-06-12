package com.example.blank

<<<<<<< HEAD
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
import java.util.*

class TextHighlightActivity : AppCompatActivity() {

    private val textView: TextView by lazy {
        findViewById(R.id.textView)
    }

    private val answerWordQueue: Queue<String> = LinkedList()
=======
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
>>>>>>> 05007cf4ca6b471f332d1644078af32e52a6ae2c

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_highlight)

<<<<<<< HEAD
        textView.text = "The sun rises in the east and sets in the west."

        initTextView()
    }
=======
        textView = findViewById(R.id.textView)
        val intent = intent
        val ocrText = intent.getStringExtra("text")
>>>>>>> 05007cf4ca6b471f332d1644078af32e52a6ae2c

    private fun initTextView() {
        textView.movementMethod = CustomLinkMovementMethod() // CustomLinkMovementMethod 설정

<<<<<<< HEAD
        val text = textView.text.toString()
        val words = text.split(" ")

        val spannableString = SpannableString(text)
        for (word in words) {
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(view: View) {
                    answerWordQueue.add(word) // 클릭한 단어를 answerWordQueue에 추가
                    replaceWordWithUnderscore(textView, word) // 단어를 _로 변경
                }
=======
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
>>>>>>> 05007cf4ca6b471f332d1644078af32e52a6ae2c
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

        initMakeProblemButton()
    }
<<<<<<< HEAD
}


//        val intent = intent
//        val ocrText = intent.getStringExtra("text")
//
//        textView.text = ocrText

//    private fun initMakeProblemButton() {
//        makeProblem.setOnClickListener {
//            intent = Intent(this, com.example.blank.TextHighlightActivity::class.java)
//            intent.putExtra("text",OCRtext.text)
//            startActivity(intent)
//        }
//    }
=======
>>>>>>> 05007cf4ca6b471f332d1644078af32e52a6ae2c

    private fun initMakeProblemButton() {
        val makeProblemButton: Button = findViewById(R.id.makeProblemButton)
        makeProblemButton.setOnClickListener {
            val intent = Intent(this, TextHighlightActivity::class.java)
            intent.putExtra("text", textView.text.toString())
            startActivity(intent)
        }
    }
}