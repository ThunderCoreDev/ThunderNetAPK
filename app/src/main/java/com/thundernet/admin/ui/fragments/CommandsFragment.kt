package com.thundernet.admin.ui.fragments

import android.os.Bundle
import android.view.View
import com.thundernet.admin.R
import com.thundernet.admin.databinding.FragmentCommandsBinding

class CommandsFragment : BaseModuleFragment(R.layout.fragment_commands) {
    private var _binding: FragmentCommandsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCommandsBinding.bind(view)

        binding.btnExecute.setOnClickListener {
    val cmd = binding.etCommand.text.toString().trim()
    lifecycleScope.launch {
        val ok = repo.testConnection() && repo.executeCommand(cmd)
        val msg = if (ok) "Comando ejecutado: $cmd" else "No hay conexión al servidor"
        com.thundernet.admin.util.showSnack(view, msg)
    }
}
}