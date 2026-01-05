package com.thundernet.web

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.webkit.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.thundernet.admin.R

class WebActivity : AppCompatActivity() {

    // ✅ CONSTANTE TAG definida DENTRO de la clase
    companion object {
        private const val TAG = "WebActivity"
        private const val ASSET_PATH = "file:///android_asset/web/index.html"
    }

    private lateinit var webView: WebView
    private lateinit var splashLayout: View
    private lateinit var preferences: SharedPreferences
    private var loadingAnimationHandler: Handler? = null
    private var loadingAnimationRunnable: Runnable? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate iniciado")
        
        try {
            // Inicializar SharedPreferences
            preferences = PreferenceManager.getDefaultSharedPreferences(this)
            
            // Configurar modo oscuro
            val darkModeEnabled = preferences.getBoolean("dark_mode", false)
            AppCompatDelegate.setDefaultNightMode(
                if (darkModeEnabled) AppCompatDelegate.MODE_NIGHT_YES 
                else AppCompatDelegate.MODE_NIGHT_NO
            )
            
            // Mostrar pantalla de carga
            try {
                setContentView(R.layout.splash_layout)
                splashLayout = findViewById(R.id.splashLayout)
                setupSplashAnimations()
                Log.d(TAG, "Splash layout cargado")
            } catch (e: Exception) {
                Log.e(TAG, "Error cargando splash: ${e.message}")
                setContentView(android.R.layout.simple_list_item_1)
            }
            
            // Esperar y cargar WebView
            Handler(Looper.getMainLooper()).postDelayed({
                loadWebView()
            }, 2000)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error en onCreate: ${e.message}")
            showErrorCrash(e.message ?: "Error desconocido")
        }
    }

    private fun setupSplashAnimations() {
        try {
            val loadingCircle = findViewById<ImageView?>(R.id.loadingCircle)
            val wowLogo = findViewById<ImageView?>(R.id.wowLogo)
            val loadingText = findViewById<TextView?>(R.id.loadingText)
            
            loadingCircle?.let { circle ->
                val rotateAnimation = RotateAnimation(
                    0f, 360f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f
                ).apply {
                    duration = 1200
                    repeatCount = Animation.INFINITE
                    interpolator = android.view.animation.LinearInterpolator()
                }
                circle.startAnimation(rotateAnimation)
            }
            
            wowLogo?.alpha = 0f
            wowLogo?.animate()
                ?.alpha(1f)
                ?.setDuration(1000)
                ?.setStartDelay(200)
                ?.start()
            
            loadingText?.let {
                animateLoadingText(it)
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error configurando animaciones: ${e.message}")
        }
    }
    
    private fun animateLoadingText(textView: TextView) {
        var dotCount = 0
        val maxDots = 3
        
        loadingAnimationHandler = Handler(Looper.getMainLooper())
        loadingAnimationRunnable = object : Runnable {
            override fun run() {
                dotCount = (dotCount + 1) % (maxDots + 1)
                val dots = ".".repeat(dotCount)
                textView.text = "Conectando$dots"
                loadingAnimationHandler?.postDelayed(this, 500)
            }
        }
        
        loadingAnimationRunnable?.let {
            loadingAnimationHandler?.post(it)
        }
    }
    
    private fun stopSplashAnimations() {
        try {
            val loadingCircle = findViewById<ImageView?>(R.id.loadingCircle)
            loadingCircle?.clearAnimation()
            
            val wowLogo = findViewById<ImageView?>(R.id.wowLogo)
            wowLogo?.clearAnimation()
            
            loadingAnimationRunnable?.let {
                loadingAnimationHandler?.removeCallbacks(it)
            }
            loadingAnimationHandler = null
            loadingAnimationRunnable = null
            
        } catch (e: Exception) {
            Log.e(TAG, "Error deteniendo animaciones: ${e.message}")
        }
    }

    private fun loadWebView() {
        Log.d(TAG, "loadWebView iniciado")
        
        stopSplashAnimations()
        
        try {
            setContentView(R.layout.activity_main)
            
            webView = findViewById(R.id.webView) ?: throw IllegalStateException("WebView no encontrado")
            val progressBar = findViewById<ProgressBar?>(R.id.progressBar)
            val errorLayout = findViewById<LinearLayout?>(R.id.errorLayout)
            val retryButton = findViewById<Button?>(R.id.retryButton)
            val menuButton = findViewById<ImageButton?>(R.id.menuButton)
            
            setupLocalWebView(progressBar)
            
            menuButton?.setOnClickListener {
                showLocalMenu(it)
            }
            
            retryButton?.setOnClickListener {
                errorLayout?.visibility = View.GONE
                webView.visibility = View.VISIBLE
                loadLocalWebApp()
            }
            
            loadLocalWebApp()
            
            Log.d(TAG, "Web local cargada exitosamente")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error en loadWebView: ${e.message}")
            showErrorCrash("No se pudo cargar la aplicación: ${e.message}")
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupLocalWebView(progressBar: ProgressBar?) {
        try {
            val webSettings = webView.settings
            webSettings.javaScriptEnabled = true
            webSettings.allowFileAccess = true
            webSettings.allowContentAccess = true
            webSettings.domStorageEnabled = true
            webSettings.databaseEnabled = true
            webSettings.allowFileAccessFromFileURLs = true
            webSettings.allowUniversalAccessFromFileURLs = true
            webSettings.loadWithOverviewMode = true
            webSettings.useWideViewPort = true
            
            webView.webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                    progressBar?.visibility = View.VISIBLE
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    progressBar?.visibility = View.GONE
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    if (request?.url?.toString()?.startsWith("file://") == false) {
                        showError("Error: ${error?.description}")
                    }
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error configurando WebView: ${e.message}")
        }
    }
    
    private fun loadLocalWebApp() {
        try {
            Log.d(TAG, "Cargando desde assets: $ASSET_PATH")
            webView.loadUrl(ASSET_PATH)
        } catch (e: Exception) {
            Log.e(TAG, "Error cargando asset: ${e.message}")
            showError("No se pudo cargar el contenido local")
        }
    }
    
    private fun showLocalMenu(view: View) {
        val menuItems = arrayOf(
            "🔄 Actualizar",
            "🧹 Limpiar Caché",
            "ℹ️ Acerca de"
        )
        
        AlertDialog.Builder(this)
            .setTitle("⚡ ThunderNet (Local)")
            .setItems(menuItems) { dialog, which ->
                dialog.dismiss()
                when (which) {
                    0 -> {
                        webView.reload()
                        Toast.makeText(this, "✅ Actualizado", Toast.LENGTH_SHORT).show()
                    }
                    1 -> {
                        webView.clearCache(true)
                        Toast.makeText(this, "🧹 Caché limpiado", Toast.LENGTH_SHORT).show()
                    }
                    2 -> showAboutDialog()
                }
            }
            .show()
    }
    
    private fun showAboutDialog() {
        val message = """
            ⚡ ThunderNet WoW ⚡
            
            Versión: 1.0.0
            
            Aplicación web local
            Cargada desde assets
            
            © ThunderNet
        """.trimIndent()
        
        AlertDialog.Builder(this)
            .setTitle("ℹ️ Acerca de")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
    
    private fun showError(message: String) {
        try {
            val errorLayout = findViewById<LinearLayout?>(R.id.errorLayout)
            val errorText = findViewById<TextView?>(R.id.errorMessage)
            
            errorText?.text = "❌ $message"
            errorLayout?.visibility = View.VISIBLE
            webView.visibility = View.GONE
            
        } catch (e: Exception) {
            Log.e(TAG, "Error mostrando error: ${e.message}")
        }
    }
    
    private fun showErrorCrash(message: String) {
        setContentView(android.R.layout.simple_list_item_1)
        val textView: TextView = findViewById(android.R.id.text1)
        textView.text = "❌ Error: $message\n\nReinstala la aplicación."
        textView.gravity = android.view.Gravity.CENTER
        textView.setTextColor(Color.WHITE)
        textView.setBackgroundColor(Color.parseColor("#0A1428"))
        textView.setPadding(20, 20, 20, 20)
    }
    
    private fun Int.dpToPx(): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            resources.displayMetrics
        ).toInt()
    }
    
    // ✅ MÉTODOS DEL CICLO DE VIDA DENTRO DE LA CLASE
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
    }
    
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
    }
    
    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }
    
    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
        stopSplashAnimations()
    }
    
    override fun onBackPressed() {
        if (::webView.isInitialized && webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
    
}  // ✅ FIN de la clase - NO PONGAS NADA DESPUÉS DE ESTO