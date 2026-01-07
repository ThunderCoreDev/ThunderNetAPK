package com.thundernet.admin.ui.fragments

import android.os.Bundle
import android.view.View
import com.thundernet.admin.R
import com.thundernet.admin.databinding.FragmentBroadcastBinding

class BroadcastFragment : BaseModuleFragment(R.layout.fragment_broadcast) {
    private var _binding: FragmentBroadcastBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBroadcastBinding.bind(view)

        binding.btnSend.setOnClickListener {
    val msgText = binding.etMessage.text.toString().trim()
    lifecycleScope.launch {
        val ok = repo.testConnection() && repo.broadcast(msgText)
        val msg = if (ok) "Broadcast enviado" else "No hay conexión al servidor"
        com.thundernet.admin.util.showSnack(view, msg)
    }
}
}