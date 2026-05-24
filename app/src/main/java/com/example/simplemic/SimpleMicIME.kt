package com.example.simplemic

import android.content.Context
import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast

class SimpleMicIME : InputMethodService() {

    override fun onCreateInputView(): View {
        val view = layoutInflater.inflate(R.layout.keyboard_view, null)
        
        val micButton = view.findViewById<ImageButton>(R.id.mic_button)
        micButton?.setOnClickListener {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            val token = window?.window?.attributes?.token
            
            // ค้นหา ID ของ Google Voice Typing โดยอัตโนมัติ
            val list = imm.enabledInputMethodList
            var googleVoiceImeId: String? = null
            
            for (info in list) {
                // ตรวจสอบ ID ที่เป็นของ Google Voice Typing (อาจต่างกันไปตามรุ่นมือถือ)
                if (info.id.contains("com.google.android.tts") || 
                    info.id.contains("com.google.android.googlequicksearchbox")) {
                    if (info.id.lowercase().contains("voice")) {
                        googleVoiceImeId = info.id
                        break
                    }
                }
            }
            
            if (googleVoiceImeId != null) {
                try {
                    // สั่งสลับไปที่ Google Voice Typing ทันที
                    imm.setInputMethod(token, googleVoiceImeId)
                } catch (e: Exception) {
                    // ถ้าสลับตรงๆ ไม่ได้ ให้แสดงหน้าต่างเลือก
                    imm.showInputMethodPicker()
                }
            } else {
                // ถ้าหาไม่เจอจริงๆ ให้ User เลือกเอง
                Toast.makeText(this, "ไม่พบ Google Voice Typing กรุณาเลือกเอง", Toast.LENGTH_SHORT).show()
                imm.showInputMethodPicker()
            }
        }

        // ปุ่มตัวเลข
        val listener = View.OnClickListener { v ->
            val b = v as Button
            currentInputConnection?.commitText(b.text, 1)
        }

        val buttonIds = arrayOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btn_dot
        )
        for (id in buttonIds) {
            view.findViewById<Button>(id)?.setOnClickListener(listener)
        }

        view.findViewById<Button>(R.id.btn_del)?.setOnClickListener {
            currentInputConnection?.deleteSurroundingText(1, 0)
        }

        return view
    }
}
