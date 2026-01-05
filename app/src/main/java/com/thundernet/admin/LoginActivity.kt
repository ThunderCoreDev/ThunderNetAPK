package com.thundernet.admin

import android.annotation.SuppressLint
import com.thundernet.web.WebActivity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class LoginActivity : AppCompatActivity() {
    
    private lateinit var webView: WebView
    private lateinit var prefs: SharedPreferences
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Configurar pantalla completa
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        
        setContentView(R.layout.activity_login)
        
        prefs = getSharedPreferences("ThunderNetAdmin", Context.MODE_PRIVATE)
        webView = findViewById(R.id.loginWebView)
        
        loadLoginPage()
    }
    
    @SuppressLint("SetJavaScriptEnabled")
    private fun loadLoginPage() {
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.allowFileAccess = true
        webSettings.allowContentAccess = true
        
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                
                // Inyectar JavaScript para manejar login
                view?.evaluateJavascript("""
                    if (typeof document !== 'undefined') {
                        var form = document.getElementById('loginForm');
                        if (form) {
                            form.addEventListener('submit', function(e) {
                                e.preventDefault();
                                var username = document.getElementById('username').value;
                                var password = document.getElementById('password').value;
                                Android.handleLogin(username, password);
                            });
                        }
                        
                        // Aplicar tema Battlenet
                        document.body.style.background = 'linear-gradient(135deg, #0a1128 0%, #1c2b45 100%)';
                        var loginBox = document.getElementById('loginBox');
                        if (loginBox) {
                            loginBox.style.background = 'rgba(11, 22, 34, 0.95)';
                            loginBox.style.border = '1px solid #00aeff';
                        }
                        var title = document.getElementById('loginTitle');
                        if (title) title.style.color = '#00aeff';
                        var btn = document.getElementById('submitBtn');
                        if (btn) {
                            btn.style.background = 'linear-gradient(90deg, #0066cc, #00aeff)';
                            btn.style.border = 'none';
                        }
                    }
                """, null)
            }
        }
        
        // Interface JavaScript
        webView.addJavascriptInterface(LoginJSInterface(), "Android")
        
        webView.loadUrl("file:///android_asset/login/login.html")
    }
    
    private fun authenticate(username: String, password: String) {
        val savedUsername = prefs.getString("admin_username", "")
        val savedPassword = prefs.getString("admin_password", "")
        
        // Encriptar contraseña ingresada
        val encryptedPassword = sha256(password)
        
        if (username == savedUsername && encryptedPassword == savedPassword) {
            prefs.edit().putBoolean("is_logged_in", true).apply()
            
            // Animación de éxito
            webView.evaluateJavascript("""
                if (typeof document !== 'undefined') {
                    var loginBox = document.getElementById('loginBox');
                    if (loginBox) {
                        loginBox.style.animation = 'successPulse 0.5s';
                    }
                    setTimeout(function() {
                        Android.loginSuccess();
                    }, 500);
                }
            """, null)
            
        } else {
            webView.evaluateJavascript("""
                if (typeof document !== 'undefined') {
                    var errorMsg = document.getElementById('errorMessage');
                    if (errorMsg) {
                        errorMsg.style.display = 'block';
                        errorMsg.textContent = 'Usuario o contraseña incorrectos';
                    }
                    var loginBox = document.getElementById('loginBox');
                    if (loginBox) {
                        loginBox.style.animation = 'shake 0.5s';
                    }
                }
            """, null)
        }
    }
    
    private fun sha256(input: String): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val bytes = digest.digest(input.toByteArray(Charsets.UTF_8))
            bytes.joinToString("") { "%02x".format(it) }
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            input.hashCode().toString()
        }
    }
    
    private fun loginSuccess() {
        startActivity(Intent(this, WebActivity::class.java))
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
    
    override fun onBackPressed() {
        // No permitir volver atrás desde login
        finishAffinity()
    }
    
    inner class LoginJSInterface {
        @JavascriptInterface
        fun handleLogin(username: String, password: String) {
            runOnUiThread {
                authenticate(username, password)
            }
        }
        
        @JavascriptInterface
        fun loginSuccess() {
            runOnUiThread {
                this@LoginActivity.loginSuccess()
            }
        }
    }
}