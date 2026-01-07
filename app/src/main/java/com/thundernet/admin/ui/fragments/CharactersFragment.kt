package com.thundernet.admin.ui.fragments

import android.os.Bundle
import android.view.View
import com.thundernet.admin.R
import com.thundernet.admin.databinding.FragmentCharactersBinding

class CharactersFragment : BaseModuleFragment(R.layout.fragment_characters) {
    private var _binding: FragmentCharactersBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCharactersBinding.bind(view)

        binding.btnResetTalents.setOnClickListener {
    val charName = binding.etCharacter.text.toString().trim()
    lifecycleScope.launch {
        val ok = repo.testConnection() && repo.resetTalents(charName)
        val msg = if (ok) "Talentos reseteados: $charName" else "No hay conexión al servidor"
        com.thundernet.admin.util.showSnack(view, msg)
    }
}
binding.btnTeleport.setOnClickListener {
    val charName = binding.etCharacter.text.toString().trim()
    lifecycleScope.launch {
        // Ejemplo: mapa 0 (Eastern Kingdoms) y coords dummy
        val ok = repo.testConnection() && repo.teleport(charName, 0, -8913.0f, 554.0f, 94.0f)
        val msg = if (ok) "Teletransportado: $charName" else "No hay conexión al servidor"
        com.thundernet.admin.util.showSnack(view, msg)
    }
}
}