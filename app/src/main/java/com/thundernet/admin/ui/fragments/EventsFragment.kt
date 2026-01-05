package com.thundenet.admin.ui.fragments

import android.os.Bundle
import android.view.View
import com.thundenet.admin.R
import com.thundenet.admin.databinding.FragmentEventsBinding

class EventsFragment : BaseModuleFragment(R.layout.fragment_events) {
    private var _binding: FragmentEventsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEventsBinding.bind(view)

        binding.btnStartEvent.setOnClickListener {
    lifecycleScope.launch {
        val ok = repo.testConnection() && repo.startEvent(1)
        val msg = if (ok) "Evento iniciado" else "No hay conexión al servidor"
        com.thundenet.admin.util.showSnack(view, msg)
    }
}
binding.btnStopEvent.setOnClickListener {
    lifecycleScope.launch {
        val ok = repo.testConnection() && repo.stopEvent(1)
        val msg = if (ok) "Evento detenido" else "No hay conexión al servidor"
        com.thundenet.admin.util.showSnack(view, msg)
    }
}
}