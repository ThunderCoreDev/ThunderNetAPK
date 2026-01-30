package com.thunderia

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Mostrar el splash por 2 segundos y luego abrir el chat
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, ChatActivity::class.java))
            finish()
        }, 2000)
    }
}