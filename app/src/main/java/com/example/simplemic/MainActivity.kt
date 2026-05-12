package com.example.simplemic

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.content.Context

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ปุ่มที่ 1: พาไปหน้า "จัดการคีย์บอร์ด" ใน Settings
        findViewById<Button>(R.id.setup_button).setOnClickListener {
            val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
            startActivity(intent)
        }

        // ปุ่มที่ 2: เรียกหน้าต่างให้เลือกคีย์บอร์ดที่จะใช้ปัจจุบัน
        findViewById<Button>(R.id.select_button).setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showInputMethodPicker()
        }
    }
}
