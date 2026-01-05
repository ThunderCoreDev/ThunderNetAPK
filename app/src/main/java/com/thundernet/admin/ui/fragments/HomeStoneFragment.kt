package com.thundenet.admin.ui.fragments

import android.os.Bundle
import android.view.View
import com.thundenet.admin.R
import com.thundenet.admin.databinding.FragmentHomestoneBinding

class HomeStoneFragment : BaseModuleFragment(R.layout.fragment_homestone) {
    private var _binding: FragmentHomestoneBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomestoneBinding.bind(view)

        binding.btnUnlockTeleport.setOnClickListener {
    val char = binding.etCharacter.text.toString().trim()
    lifecycleScope.launch {
        val ok = repo.testConnection() && repo.homeStoneTeleport(char)
        val msg = if (ok) "Personaje destrabado y enviado a piedra hogar" else "No hay conexión al servidor"
        com.thundenet.admin.util.showSnack(view, msg)
    }
}
}