package com.example.simplemic

import android.content.Intent
import android.content.pm.PackageManager
import android.inputmethodservice.InputMethodService
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import android.Manifest
import android.widget.Toast

class SimpleMicIME : InputMethodService() {

    private var speechRecognizer: SpeechRecognizer? = null
    private var recognitionIntent: Intent? = null

    override fun onCreate() {
        super.onCreate()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        recognitionIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "th-TH") // ตั้งค่าเป็นภาษาไทย
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }

        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {
                val message = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                    SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                    SpeechRecognizer.ERROR_NETWORK -> "Network error"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                    SpeechRecognizer.ERROR_NO_MATCH -> "No match"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService busy"
                    SpeechRecognizer.ERROR_SERVER -> "Error from server"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                    else -> "Unknown error"
                }
                Toast.makeText(applicationContext, "Error: $message", Toast.LENGTH_SHORT).show()
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    currentInputConnection?.commitText(matches[0], 1)
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                // สามารถแสดงตัวอย่างข้อความขณะกำลังพูดได้ที่นี่
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    override fun onCreateInputView(): View {
        val view = layoutInflater.inflate(R.layout.keyboard_view, null)
        
        val micButton = view.findViewById<ImageButton>(R.id.mic_button)
        micButton?.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                speechRecognizer?.startListening(recognitionIntent)
                Toast.makeText(this, "กำลังฟัง...", Toast.LENGTH_SHORT).show()
            } else {
                // ถ้ายังไม่มีสิทธิ์ ให้เปิด PermissionActivity
                val intent = Intent(this, PermissionActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(intent)
            }
        }

        // ฟังก์ชันส่งตัวอักษร/ตัวเลข
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

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer?.destroy()
    }
}
