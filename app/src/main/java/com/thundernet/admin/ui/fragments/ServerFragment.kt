package com.thundernet.admin.ui.fragments

import android.os.Bundle
import android.view.View
import com.thundernet.admin.R
import com.thundernet.admin.databinding.FragmentServerBinding

class ServerFragment : BaseModuleFragment(R.layout.fragment_server) {
    private var _binding: FragmentServerBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentServerBinding.bind(view)

        binding.btnRestart.setOnClickListener {
            guardOnline({ repo.restartServer() }, view, "Servidor reiniciado")
        }
        binding.btnStatus.setOnClickListener {
            val status = if (repo.testConnection()) "Online" else "Offline"
            com.thundernet.admin.util.showSnack(view, "Estado del servidor: $status")
        }
    }
}