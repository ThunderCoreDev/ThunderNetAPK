package com.thundenet.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.thundenet.admin.databinding.ActivityMainBinding
import com.thundenet.admin.ui.adapters.ModulesPagerAdapter
import kotlin.math.abs

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    // Lista de títulos con emojis
    private val tabTitles = listOf(
        "👥 Jugadores", "🧑 Personajes", "💰 Economía", "📦 Ítems", "🖥️ Servidor",
        "🎫 Tickets", "📅 Eventos", "⌨️ Comandos", "📢 Broadcast", "🏠 Piedra hogar",
        "⚙️ Config", "📋 Logs", "📊 Estadísticas", "ℹ️ Acerca de"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_ThunderNetAdmin) // asegúrate que el nombre del tema coincida con tu themes.xml
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = ModulesPagerAdapter(this)
        binding.viewPager.adapter = adapter

        // Animación de transición entre páginas
        binding.viewPager.setPageTransformer { page, position ->
            page.alpha = 0.2f + (1 - abs(position)) * 0.8f
            page.translationX = -position * page.width * 0.2f
        }

        // Asignar títulos con emojis a cada tab
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }
}