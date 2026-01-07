package com.thundenet.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.thundenet.admin.databinding.ActivityMainBinding
import com.thundenet.admin.ui.adapters.ModulesPagerAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val tabTitles = listOf(
        "Jugadores", "Personajes", "Economía", "Ítems", "Servidor",
        "Tickets", "Eventos", "Comandos", "Broadcast", "Piedra hogar",
        "Config", "Logs", "Estadísticas", "Acerca de"
    )

    private val tabIcons = listOf(
        R.drawable.ic_people, R.drawable.ic_person, R.drawable.ic_attach_money, R.drawable.ic_inventory, R.drawable.ic_server,
        R.drawable.ic_ticket, R.drawable.ic_event, R.drawable.ic_terminal, R.drawable.ic_broadcast, R.drawable.ic_home,
        R.drawable.ic_settings, R.drawable.ic_list, R.drawable.ic_insights, R.drawable.ic_info
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_ThunderNetAdmin)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = ModulesPagerAdapter(this)
        binding.viewPager.adapter = adapter
        binding.viewPager.setPageTransformer { page, position ->
            page.alpha = 0.2f + (1 - kotlin.math.abs(position)) * 0.8f
            page.translationX = -position * page.width * 0.2f
        }

        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
            tab.setIcon(tabIcons[position])
        }.attach()
    }
}