package com.example.simplemic

import android.inputmethodservice.InputMethodService
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.view.inputmethod.InputMethodManager
import android.content.Context

class SimpleMicIME : InputMethodService() {

    override fun onCreateInputView(): View {
        val view = layoutInflater.inflate(R.layout.keyboard_view, null)
        
        // 1. ปุ่มไมค์ - สลับไปใช้ Google Voice Typing โดยตรง
        view.findViewById<ImageButton>(R.id.mic_button)?.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val token = window?.window?.attributes?.token
            
            try {
                // เรียก Service พิมพ์ด้วยเสียงของ Google (ตัวมาตรฐาน)
                imm.setInputMethod(token, "com.google.android.googlequicksearchbox/com.google.android.voicesearch.ime.VoiceInputMethodService")
            } catch (e: Exception) {
                // ถ้าเรียกตรงๆ ไม่ได้ ให้เปิดหน้าต่างเลือกคีย์บอร์ดให้ User จิ้มเอง
                imm.showInputMethodPicker()
            }
        }

        // 2. ฟังก์ชันส่งตัวอักษร/ตัวเลข
        val listener = View.OnClickListener { v ->
            val b = v as Button
            currentInputConnection?.commitText(b.text, 1)
        }

        // 3. เชื่อมปุ่มตัวเลขและจุด (ID ต้องตรงกับ XML)
        val buttonIds = arrayOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btn_dot
        )
        
        for (id in buttonIds) {
            view.findViewById<Button>(id)?.setOnClickListener(listener)
        }

        // 4. ปุ่มลบ (Backspace)
        view.findViewById<Button>(R.id.btn_del)?.setOnClickListener {
            currentInputConnection?.deleteSurroundingText(1, 0)
        }

        return view
    }
}
