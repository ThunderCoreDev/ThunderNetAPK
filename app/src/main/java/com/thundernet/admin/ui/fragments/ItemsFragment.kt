package com.thundernet.admin.ui.fragments

import android.os.Bundle
import android.view.View
import com.thundernet.admin.R
import com.thundernet.admin.databinding.FragmentItemsBinding

class ItemsFragment : BaseModuleFragment(R.layout.fragment_items) {
    private var _binding: FragmentItemsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentItemsBinding.bind(view)

        binding.btnGiveItem.setOnClickListener {
    val char = binding.etCharacter.text.toString().trim()
    val itemId = binding.etItemId.text.toString().toIntOrNull() ?: 0
    val count = binding.etCount.text.toString().toIntOrNull() ?: 1
    lifecycleScope.launch {
        val ok = repo.testConnection() && repo.giveItem(char, itemId, count)
        val msg = if (ok) "Ítem entregado: $itemId x$count a $char" else "No hay conexión al servidor"
        com.thundernet.admin.util.showSnack(view, msg)
    }
}
}