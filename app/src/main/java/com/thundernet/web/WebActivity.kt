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

    private lateinit var webView: WebView
    private lateinit var splashLayout: View
    private lateinit var preferences: SharedPreferences
    private var currentUrl: String = "http://172.16.1.1"
    private var loadingAnimationHandler: Handler? = null
    private var loadingAnimationRunnable: Runnable? = null
    
    companion object {
        private const val TAG = "ThunderNetApp"
        private const val PREF_SERVER_URL = "server_url"
        private const val DEFAULT_URL = "http://172.16.1.1"
        private const val PREF_DARK_MODE = "dark_mode"
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate iniciado")
        
        try {
            // Inicializar SharedPreferences
            preferences = PreferenceManager.getDefaultSharedPreferences(this)
            
            // Obtener la URL guardada
            currentUrl = preferences.getString(PREF_SERVER_URL, DEFAULT_URL) ?: DEFAULT_URL
            Log.d(TAG, "URL a cargar: $currentUrl")
            
            // Configurar modo oscuro desde preferencias
            val darkModeEnabled = preferences.getBoolean(PREF_DARK_MODE, false)
            AppCompatDelegate.setDefaultNightMode(
                if (darkModeEnabled) AppCompatDelegate.MODE_NIGHT_YES 
                else AppCompatDelegate.MODE_NIGHT_NO
            )
            
            // Mostrar pantalla de carga con animaciones
            try {
                setContentView(R.layout.splash_layout)
                splashLayout = findViewById(R.id.splashLayout)
                
                // Inicializar animaciones del splash
                setupSplashAnimations()
                
                Log.d(TAG, "Splash layout cargado con animaciones")
                
            } catch (e: Exception) {
                Log.e(TAG, "Error cargando splash: ${e.message}")
                // Intentar cargar layout alternativo
                setContentView(android.R.layout.simple_list_item_1)
            }
            
            // Esperar 3 segundos y cargar WebView
            Handler(Looper.getMainLooper()).postDelayed({
                loadWebView()
            }, 3000)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error en onCreate: ${e.message}")
            e.printStackTrace()
            // Mostrar mensaje de error al usuario
            showErrorCrash(e.message ?: "Error desconocido")
        }
    }

    private fun setupSplashAnimations() {
        try {
            val loadingCircle = findViewById<ImageView>(R.id.loadingCircle)
            val wowLogo = findViewById<ImageView>(R.id.wowLogo)
            val loadingText = findViewById<TextView>(R.id.loadingText)
            
            // ANIMACIÓN 1: Rotación del círculo de carga
            val rotateAnimation = RotateAnimation(
                0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            ).apply {
                duration = 1200 // 1.2 segundos por vuelta
                repeatCount = Animation.INFINITE
                interpolator = android.view.animation.LinearInterpolator()
            }
            
            loadingCircle.startAnimation(rotateAnimation)
            
            // ANIMACIÓN 2: Fade in para el logo
            wowLogo?.alpha = 0f
            wowLogo?.animate()
                ?.alpha(1f)
                ?.setDuration(1000)
                ?.setStartDelay(200)
                ?.start()
            
            // ANIMACIÓN 3: Texto "Conectando..." con puntos animados
            loadingText?.let {
                animateLoadingText(it)
            }
            
            // ANIMACIÓN 4: Parpadeo sutil del logo
            val fadeAnimation = android.view.animation.AlphaAnimation(0.7f, 1.0f).apply {
                duration = 1500
                repeatMode = android.view.animation.Animation.REVERSE
                repeatCount = android.view.animation.Animation.INFINITE
            }
            wowLogo?.startAnimation(fadeAnimation)
            
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
            // Detener animación del círculo
            val loadingCircle = findViewById<ImageView?>(R.id.loadingCircle)
            loadingCircle?.clearAnimation()
            
            // Detener animación del logo
            val wowLogo = findViewById<ImageView?>(R.id.wowLogo)
            wowLogo?.clearAnimation()
            
            // Detener animación del texto
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
        
        // Detener animaciones del splash
        stopSplashAnimations()
        
        try {
            setContentView(R.layout.activity_main)
            
            // Inicializar vistas con verificaciones
            webView = findViewById(R.id.webView) ?: throw IllegalStateException("WebView no encontrado")
            val progressBar: ProgressBar = findViewById(R.id.progressBar)
            val errorLayout: LinearLayout = findViewById(R.id.errorLayout)
            val retryButton: Button = findViewById(R.id.retryButton)
            val menuButton: ImageButton = findViewById(R.id.menuButton)
            
            // Configurar WebView
            setupWebView(progressBar)
            
            // Configurar botón de menú
            menuButton?.setOnClickListener {
                showOptionsMenu(it)
            }
            
            // Configurar botón de reintentar
            retryButton?.setOnClickListener {
                errorLayout?.visibility = View.GONE
                webView.visibility = View.VISIBLE
                loadUrl(currentUrl)
            }
            
            // Cargar URL
            loadUrl(currentUrl)
            
            Log.d(TAG, "WebView cargado exitosamente")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error en loadWebView: ${e.message}")
            e.printStackTrace()
            showErrorCrash("No se pudo cargar la aplicación: ${e.message}")
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(progressBar: ProgressBar) {
        try {
            val webSettings = webView.settings
            webSettings.javaScriptEnabled = true
            webSettings.domStorageEnabled = true
            webSettings.setSupportZoom(true)
            webSettings.builtInZoomControls = true
            webSettings.displayZoomControls = false
            
            // Habilitar acceso a archivos locales si es necesario
            webSettings.allowFileAccess = true
            webSettings.allowContentAccess = true
            
            // Mejoras para mejor rendimiento
            webSettings.cacheMode = WebSettings.LOAD_DEFAULT
            webSettings.loadWithOverviewMode = true
            webSettings.useWideViewPort = true
            
            webView.webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                    progressBar.visibility = View.VISIBLE
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    progressBar.visibility = View.GONE
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    showError("Error al cargar la página: ${error?.description}")
                }
            }
            
            // Manejar errores de SSL/TLS
            webView.webChromeClient = WebChromeClient()
            
        } catch (e: Exception) {
            Log.e(TAG, "Error configurando WebView: ${e.message}")
        }
    }
    
    private fun loadUrl(url: String) {
        try {
            Log.d(TAG, "Cargando URL: $url")
            webView.loadUrl(url)
        } catch (e: Exception) {
            Log.e(TAG, "Error cargando URL: ${e.message}")
            showError("No se pudo cargar la página")
        }
    }
    
    private fun showOptionsMenu(view: View) {
        try {
            val menuItems = listOf(
                "🔄 Actualizar",
                "⚙️ Configurar URL", 
                "🌙 Modo Oscuro",
                "🧹 Limpiar Caché",
                "ℹ️ Acerca de"
            )
            
            // Crear un diálogo simple con botones personalizados
            val dialog = AlertDialog.Builder(this)
                .setTitle("⚡ ThunderNet WoW")
                .setItems(menuItems.toTypedArray()) { dialog, which ->
                    dialog.dismiss()
                    when (which) {
                        0 -> { // Actualizar
                            webView.reload()
                            showToast("✅ Página actualizada")
                        }
                        1 -> { // Configurar URL
                            showUrlConfigDialog()
                        }
                        2 -> { // Modo Oscuro
                            toggleDarkMode()
                        }
                        3 -> { // Limpiar Caché
                            clearCache()
                        }
                        4 -> { // Acerca de
                            showAboutDialog()
                        }
                    }
                }
                .create()
            
            // Mostrar el diálogo
            dialog.show()
            
            // PERSONALIZACIÓN: Título (DEBE ser después de show())
            dialog.findViewById<TextView>(android.R.id.title)?.apply {
                setTextColor(Color.parseColor("#00B4FF")) // Azul eléctrico
                textSize = 18f
                gravity = Gravity.CENTER
                setPadding(0, 24.dpToPx(), 0, 16.dpToPx())
            }
            
            // PERSONALIZACIÓN: Fondo del diálogo
            try {
                dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
            } catch (e: Exception) {
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#0A1428")))
            }
            
            // PERSONALIZACIÓN: Lista de items
            val listView = dialog.window?.decorView?.findViewById<ListView>(android.R.id.list)
            listView?.apply {
                // Fondo azul oscuro
                setBackgroundColor(Color.parseColor("#0A1428"))
                
                // Eliminar divisores
                divider = null
                dividerHeight = 0
                
                // Personalizar CADA TextView en la lista
                for (i in 0 until childCount) {
                    val child = getChildAt(i)
                    if (child is TextView) {
                        // Texto BLANCO
                        child.setTextColor(Color.WHITE)
                        child.textSize = 16f
                        child.setPadding(24.dpToPx(), 16.dpToPx(), 24.dpToPx(), 16.dpToPx())
                        child.minHeight = 48.dpToPx()
                        child.gravity = Gravity.START or Gravity.CENTER_VERTICAL
                        
                        // Fondo transparente por defecto
                        child.setBackgroundColor(Color.TRANSPARENT)
                    }
                }
                
                // Configurar listener para efecto de presionado
                setOnItemClickListener { parent, view, position, id ->
                    (view as? TextView)?.apply {
                        // Efecto temporal al hacer clic
                        setBackgroundColor(Color.parseColor("#1A3563"))
                        postDelayed({
                            setBackgroundColor(Color.TRANSPARENT)
                        }, 150)
                    }
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error mostrando menú: ${e.message}")
            showToast("Error al mostrar menú")
        }
    }
    
    private fun showUrlConfigDialog() {
        try {
            // Crear el layout personalizado
            val layout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(24.dpToPx(), 16.dpToPx(), 24.dpToPx(), 8.dpToPx())
                setBackgroundColor(Color.parseColor("#0A1428"))
            }
            
            // Título
            val title = TextView(this).apply {
                text = "⚙️ Configurar URL del servidor"
                setTextColor(Color.parseColor("#00B4FF")) // Azul eléctrico
                textSize = 18f
                gravity = Gravity.CENTER
                setPadding(0, 0, 0, 16.dpToPx())
            }
            
            // EditText personalizado
            val editText = EditText(this).apply {
                setText(currentUrl)
                hint = "Ej: http://172.16.1.1"
                setTextColor(Color.WHITE)
                setHintTextColor(Color.parseColor("#CCCCCC"))
                
                // Usar el drawable si existe, sino usar color sólido
                try {
                    background = ContextCompat.getDrawable(this@WebActivity, R.drawable.edittext_background)
                } catch (e: Exception) {
                    setBackgroundColor(Color.parseColor("#0A1428"))
                }
                
                setPadding(16.dpToPx(), 12.dpToPx(), 16.dpToPx(), 12.dpToPx())
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 24.dpToPx()
                }
            }
            
            layout.addView(title)
            layout.addView(editText)
            
            // Crear el diálogo
            val dialog = AlertDialog.Builder(this)
                .setView(layout)
                .setPositiveButton("💾 GUARDAR") { dialog, _ ->
                    val newUrl = editText.text.toString().trim()
                    if (newUrl.isNotEmpty()) {
                        currentUrl = if (newUrl.startsWith("http")) newUrl else "http://$newUrl"
                        
                        preferences.edit()
                            .putString(PREF_SERVER_URL, currentUrl)
                            .apply()
                        
                        loadUrl(currentUrl)
                        showToast("✅ URL actualizada y guardada")
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("❌ CANCELAR") { dialog, _ ->
                    dialog.dismiss()
                }
                .setNeutralButton("🔄 PREDETERMINADA") { dialog, _ ->
                    currentUrl = DEFAULT_URL
                    preferences.edit()
                        .putString(PREF_SERVER_URL, currentUrl)
                        .apply()
                    loadUrl(currentUrl)
                    showToast("🔄 URL restaurada a predeterminada")
                    dialog.dismiss()
                }
                .create()
            
            // Mostrar el diálogo
            dialog.show()
            
            // Personalizar fondo del diálogo
            try {
                dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
            } catch (e: Exception) {
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#0A1428")))
            }
            
            // Personalizar botones después de mostrar el diálogo
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.apply {
                setTextColor(Color.parseColor("#0A1428"))
                setBackgroundColor(Color.parseColor("#00B4FF"))
                setPadding(24.dpToPx(), 12.dpToPx(), 24.dpToPx(), 12.dpToPx())
            }
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.apply {
                setTextColor(Color.parseColor("#00B4FF"))
                setBackgroundColor(Color.parseColor("#132347"))
                setPadding(24.dpToPx(), 12.dpToPx(), 24.dpToPx(), 12.dpToPx())
            }
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL)?.apply {
                setTextColor(Color.parseColor("#00B4FF"))
                setBackgroundColor(Color.parseColor("#132347"))
                setPadding(24.dpToPx(), 12.dpToPx(), 24.dpToPx(), 12.dpToPx())
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error mostrando diálogo: ${e.message}")
            showToast("Error al configurar URL")
        }
    }
    
    private fun toggleDarkMode() {
        try {
            val isDarkMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
            val newMode = if (isDarkMode) {
                AppCompatDelegate.MODE_NIGHT_NO
            } else {
                AppCompatDelegate.MODE_NIGHT_YES
            }
            
            AppCompatDelegate.setDefaultNightMode(newMode)
            
            // Guardar la preferencia del modo oscuro
            preferences.edit()
                .putBoolean(PREF_DARK_MODE, !isDarkMode)
                .apply()
            
            showToast(if (isDarkMode) "🌞 Modo claro activado" else "🌙 Modo oscuro activado")
            
            // Recargar la actividad para aplicar cambios
            recreate()
            
        } catch (e: Exception) {
            Log.e(TAG, "Error cambiando modo oscuro: ${e.message}")
            showToast("Error al cambiar modo")
        }
    }
    
    private fun clearCache() {
        try {
            webView.clearCache(true)
            webView.clearHistory()
            CookieManager.getInstance().removeAllCookies(null)
            CookieManager.getInstance().flush()
            
            showToast("🧹 Caché limpiado correctamente")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error limpiando caché: ${e.message}")
            showToast("Error al limpiar caché")
        }
    }
    
    private fun showAboutDialog() {
        try {
            val message = """
                ⚡ ThunderNet WoW ⚡
                
                Versión: 1.0.0
                
                Aplicación oficial de ThunderNet
                World of Warcraft
                
                Desarrollado con ❤️
                para la comunidad
                
                © 2026+ ThunderNet WoW
                Todos los derechos reservados
            """.trimIndent()
            
            val dialog = AlertDialog.Builder(this)
                .setTitle("ℹ️ Acerca de")
                .setMessage(message)
                .setPositiveButton("👌 ACEPTAR") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
            
            dialog.show()
            
            // Personalizar el diálogo
            try {
                dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
            } catch (e: Exception) {
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#0A1428")))
            }
            
            // Personalizar título
            dialog.findViewById<TextView>(android.R.id.title)?.apply {
                setTextColor(Color.parseColor("#00B4FF"))
                textSize = 18f
                gravity = Gravity.CENTER
                setPadding(0, 16.dpToPx(), 0, 8.dpToPx())
            }
            
            // Personalizar mensaje
            dialog.findViewById<TextView>(android.R.id.message)?.apply {
                setTextColor(Color.WHITE)
                textSize = 14f
                gravity = Gravity.CENTER
                setLineSpacing(1.2f, 1.2f)
                setPadding(16.dpToPx(), 8.dpToPx(), 16.dpToPx(), 8.dpToPx())
            }
            
            // Personalizar botón
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.apply {
                setTextColor(Color.parseColor("#0A1428"))
                setBackgroundColor(Color.parseColor("#00B4FF"))
                setPadding(24.dpToPx(), 12.dpToPx(), 24.dpToPx(), 12.dpToPx())
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error mostrando about: ${e.message}")
        }
    }
    
    private fun showError(message: String) {
        try {
            val errorLayout: LinearLayout? = findViewById(R.id.errorLayout)
            val errorText: TextView? = findViewById(R.id.errorMessage)
            
            errorText?.text = "❌ $message"
            errorLayout?.visibility = View.VISIBLE
            webView.visibility = View.GONE
            
        } catch (e: Exception) {
            Log.e(TAG, "Error mostrando error: ${e.message}")
        }
    }
    
    private fun showErrorCrash(message: String) {
        // Mostrar error fatal en pantalla completa
        setContentView(android.R.layout.simple_list_item_1)
        val textView: TextView = findViewById(android.R.id.text1)
        textView.text = "❌ Error: $message\n\n🔧 Reinstala la aplicación."
        textView.gravity = android.view.Gravity.CENTER
        textView.setTextColor(Color.WHITE)
        textView.setBackgroundColor(Color.parseColor("#0A1428"))
        textView.setPadding(20, 20, 20, 20)
    }
    
    fun showToast(message: String) {
        try {
            val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
            
         fun logout() {
    val intent = Intent(this, com.thundernet.admin.LoginActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    startActivity(intent)
    finish()
}   
            
            // Personalizar el toast si es posible
            toast.view?.apply {
                try {
                    setBackgroundResource(R.drawable.toast_background)
                } catch (e: Exception) {
                    setBackgroundColor(Color.parseColor("#CC0A1428"))
                }
                val textView = findViewById<TextView>(android.R.id.message)
                textView?.apply {
                    setTextColor(Color.WHITE)
                    gravity = Gravity.CENTER
                    setPadding(24.dpToPx(), 16.dpToPx(), 24.dpToPx(), 16.dpToPx())
                }
            }
            
            toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 100.dpToPx())
            toast.show()
            
        } catch (e: Exception) {
            // Si falla la personalización, mostrar toast normal
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun Int.dpToPx(): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            resources.displayMetrics
        ).toInt()
    }
    
    // Manejo del botón atrás
    override fun onBackPressed() {
        if (::webView.isInitialized && webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
    
    // Limpiar recursos cuando la actividad se destruye
    override fun onDestroy() {
        super.onDestroy()
        stopSplashAnimations()
        Log.d(TAG, "onDestroy - recursos limpiados")
    }
    
    // Métodos de ciclo de vida para debugging
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
}