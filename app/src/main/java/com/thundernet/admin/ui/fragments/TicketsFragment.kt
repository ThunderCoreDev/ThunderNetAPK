package com.thundernet.admin.ui.fragments

import android.os.Bundle
import android.view.View
import com.thundernet.admin.R
import com.thundernet.admin.databinding.FragmentTicketsBinding

class TicketsFragment : BaseModuleFragment(R.layout.fragment_tickets) {
    private var _binding: FragmentTicketsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTicketsBinding.bind(view)

        binding.btnCreateTicket.setOnClickListener {
    lifecycleScope.launch {
        val ok = repo.testConnection() && repo.createTicket("PlayerName", "Ayuda requerida")
        val msg = if (ok) "Ticket creado" else "No hay conexión al servidor"
        com.thundernet.admin.util.showSnack(view, msg)
    }
}
binding.btnCloseTicket.setOnClickListener {
    lifecycleScope.launch {
        val ok = repo.testConnection() && repo.closeTicket(123)
        val msg = if (ok) "Ticket cerrado" else "No hay conexión al servidor"
        com.thundernet.admin.util.showSnack(view, msg)
    }
}
}