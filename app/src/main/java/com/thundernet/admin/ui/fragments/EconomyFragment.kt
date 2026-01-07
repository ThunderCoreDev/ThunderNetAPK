package com.thundernet.admin.ui.fragments

import android.os.Bundle
import android.view.View
import com.thundernet.admin.R
import com.thundernet.admin.databinding.FragmentEconomyBinding

class EconomyFragment : BaseModuleFragment(R.layout.fragment_economy) {
    private var _binding: FragmentEconomyBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEconomyBinding.bind(view)

        binding.btnSetGold.setOnClickListener {
            val char = binding.etCharacter.text.toString().trim()
            val gold = binding.etGold.text.toString().toIntOrNull() ?: 0
            guardOnline({ /* TODO: set gold */ true }, view, "Oro ajustado: $char -> $gold")
        }
    }
}