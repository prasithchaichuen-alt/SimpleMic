package com.example.simplemic

import android.Manifest
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // สร้างปุ่มกดง่ายๆ แบบ Minimalist
        val btn = Button(this).apply {
            text = "เปิดใช้งานสิทธิ์ไมโครโฟน (กดตกลง)"
            textSize = 20f
        }
        setContentView(btn)

        btn.setOnClickListener {
            // ขอสิทธิ์ไมโครโฟน
            ActivityCompat.requestPermissions(this, 
                arrayOf(Manifest.permission.RECORD_AUDIO), 1)
        }
    }

    // เมื่อกดตกลงเสร็จแล้วให้ปิดหน้าจอนี้
    override fun onRequestPermissionsResult(rc: Int, p: Array<out String>, res: IntArray) {
        super.onRequestPermissionsResult(rc, p, res)
        finish() 
    }
}
