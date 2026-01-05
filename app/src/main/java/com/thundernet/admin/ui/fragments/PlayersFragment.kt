package com.thundenet.admin.ui.fragments

import android.os.Bundle
import android.view.View
import com.thundenet.admin.R
import com.thundenet.admin.databinding.FragmentPlayersBinding

class PlayersFragment : BaseModuleFragment(R.layout.fragment_players) {
    private var _binding: FragmentPlayersBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPlayersBinding.bind(view)

        binding.btnBan.setOnClickListener {
    val account = binding.etAccount.text.toString().trim()
    lifecycleScope.launch {
        val ok = repo.testConnection() && repo.banPlayer(account)
        val msg = if (ok) "Cuenta baneada: $account" else "No hay conexión al servidor"
        com.thundenet.admin.util.showSnack(view, msg)
    }
}
binding.btnUnban.setOnClickListener {
    val account = binding.etAccount.text.toString().trim()
    lifecycleScope.launch {
        val ok = repo.testConnection() && repo.unbanPlayer(account)
        val msg = if (ok) "Cuenta desbaneada: $account" else "No hay conexión al servidor"
        com.thundenet.admin.util.showSnack(view, msg)
    }
}
}