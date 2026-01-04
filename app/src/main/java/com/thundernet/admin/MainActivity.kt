package com.thundernet.admin

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.webkit.*
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class MainActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var prefs: SharedPreferences
    private var isFirstRun = false
    
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        prefs = getSharedPreferences("ThunderNetAdmin", Context.MODE_PRIVATE)
        isFirstRun = prefs.getBoolean("first_run", true)
        
        if (isFirstRun) {
            setupAdminAccount()
            return
        }
        
        val isLoggedIn = prefs.getBoolean("is_logged_in", false)
        if (!isLoggedIn) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        
        initializeWebView()
    }
    
    private fun setupAdminAccount() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.admin_setup_dialog, null)
        
        AlertDialog.Builder(this)
            .setTitle("Configuración de Cuenta Administrativa")
            .setMessage("Es la primera vez que ejecutas la aplicación. Necesitas crear una cuenta administrativa para la aplicación.")
            .setCancelable(false)
            .setView(dialogView)
            .setPositiveButton("Guardar") { dialog, _ ->
                val username = dialogView.findViewById<EditText>(R.id.etAdminUsername).text.toString()
                val password = dialogView.findViewById<EditText>(R.id.etAdminPassword).text.toString()
                val confirmPassword = dialogView.findViewById<EditText>(R.id.etAdminConfirmPassword).text.toString()
                val errorText = dialogView.findViewById<TextView>(R.id.tvSetupError)
                
                if (username.isEmpty() || password.isEmpty()) {
                    errorText.text = "Todos los campos son obligatorios"
                    errorText.visibility = View.VISIBLE
                    return@setPositiveButton
                } else if (password != confirmPassword) {
                    errorText.text = "Las contraseñas no coinciden"
                    errorText.visibility = View.VISIBLE
                    return@setPositiveButton
                } else if (password.length < 6) {
                    errorText.text = "La contraseña debe tener al menos 6 caracteres"
                    errorText.visibility = View.VISIBLE
                    return@setPositiveButton
                }
                
                // Guardar credenciales
                val editor = prefs.edit()
                editor.putString("admin_username", username)
                editor.putString("admin_password", sha256(password))
                editor.putBoolean("first_run", false)
                editor.putBoolean("is_logged_in", true)
                editor.apply()
                
                Toast.makeText(this, "Cuenta administrativa creada exitosamente", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                initializeWebView()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .setOnCancelListener {
                finish()
            }
            .show()
    }
    
    @SuppressLint("SetJavaScriptEnabled")
    private fun initializeWebView() {
        webView = findViewById(R.id.webView)
        
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.databaseEnabled = true
        webSettings.setSupportZoom(false)
        webSettings.builtInZoomControls = false
        webSettings.displayZoomControls = false
        webSettings.allowFileAccess = true
        webSettings.allowContentAccess = true
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true
        
        // Configurar WebViewClient para manejar errores
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }
            
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                // Inyectar configuración del servidor
                val serverIp = prefs.getString("server_ip", "localhost")
                val authDb = prefs.getString("auth_db", "auth")
                val charsDb = prefs.getString("chars_db", "characters")
                val worldDb = prefs.getString("world_db", "world")
                
                view?.evaluateJavascript("""
                    if (typeof window.serverConfig === 'undefined') {
                        window.serverConfig = {
                            ip: '$serverIp',
                            databases: {
                                auth: '$authDb',
                                characters: '$charsDb',
                                world: '$worldDb'
                            },
                            connected: false
                        };
                    }
                """, null)
            }
            
            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                // Solo cargar offline si es una página web real, no un asset local
                if (request?.url?.toString()?.startsWith("file://") == false) {
                    view?.loadUrl("file:///android_asset/admin/offline.html")
                }
            }
            
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                // Permitir carga de archivos locales
                return false
            }
        }
        
        // Configurar WebChromeClient
        webView.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                android.util.Log.d(
                    "WebView",
                    "${consoleMessage.messageLevel()}: ${consoleMessage.message()}"
                )
                return true
            }
            
            override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                AlertDialog.Builder(this@MainActivity)
                    .setMessage(message)
                    .setPositiveButton("OK") { _, _ -> result?.confirm() }
                    .setCancelable(false)
                    .show()
                return true
            }
        }
        
        // Habilitar JavaScript Interface
        webView.addJavascriptInterface(WebAppInterface(this), "Android")
        
        // Cargar la página principal
        webView.loadUrl("file:///android_asset/admin/index.html")
    }
    
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
    
    fun logout() {
        prefs.edit().putBoolean("is_logged_in", false).apply()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
    
    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    
    private fun sha256(input: String): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(input.toByteArray(Charsets.UTF_8))
            hash.joinToString("") { "%02x".format(it) }
        } catch (e: NoSuchAlgorithmException) {
            // Fallback simple (no usar en producción)
            e.printStackTrace()
            input.hashCode().toString()
        }
    }
}

// Clase WebAppInterface si la necesitas
class WebAppInterface(private val activity: MainActivity) {
    @android.webkit.JavascriptInterface
    fun logout() {
        activity.runOnUiThread {
            activity.logout()
        }
    }
    
    @android.webkit.JavascriptInterface
    fun showToast(message: String) {
        activity.runOnUiThread {
            activity.showToast(message)
        }
    }
    
    @android.webkit.JavascriptInterface
    fun getServerConfig(): String {
        // Devuelve configuración del servidor como JSON
        return """{
            "ip": "localhost",
            "databases": {
                "auth": "auth",
                "characters": "characters", 
                "world": "world"
            }
        }"""
    }
}