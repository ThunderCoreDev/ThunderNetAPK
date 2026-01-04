package com.thundernet.admin

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    
    private lateinit var prefs: SharedPreferences
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_layout)
        
        prefs = getSharedPreferences("ThunderNetAdmin", MODE_PRIVATE)
        
        // Esperar 2 segundos y luego redirigir
        Handler(Looper.getMainLooper()).postDelayed({
            checkFirstRun()
        }, 2000)
    }
    
    private fun checkFirstRun() {
        val isFirstRun = prefs.getBoolean("first_run", true)
        
        if (isFirstRun) {
            // Ir directamente a MainActivity para configurar cuenta
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            val isLoggedIn = prefs.getBoolean("is_logged_in", false)
            val destination = if (isLoggedIn) {
                MainActivity::class.java
            } else {
                LoginActivity::class.java
            }
            startActivity(Intent(this, destination))
        }
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}