package com.thundernet.admin.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.thundernet.admin.R
import com.thundernet.admin.data.db.AppDatabase
import com.thundernet.admin.data.db.AdminUser
import com.thundernet.admin.data.prefs.AppPrefs
import com.thundernet.admin.databinding.FragmentConfigBinding
import kotlinx.coroutines.launch
import java.security.MessageDigest

class ConfigFragment : BaseModuleFragment(R.layout.fragment_config) {
    private var _binding: FragmentConfigBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentConfigBinding.bind(view)

        binding.btnSaveDb.setOnClickListener {
            val host = binding.etDbHost.text.toString().trim()
            val port = binding.etDbPort.text.toString().toIntOrNull() ?: 3306
            val user = binding.etDbUser.text.toString().trim()
            val pass = binding.etDbPass.text.toString().trim()
            lifecycleScope.launch {
                AppPrefs.saveDbConfig(requireContext(), host, port, user, pass)
                com.thundernet.admin.util.showSnack(view, "DB guardada")
            }
        }

        binding.btnSaveSoap.setOnClickListener {
            val soapHost = binding.etSoapHost.text.toString().trim()
            val soapPort = binding.etSoapPort.text.toString().toIntOrNull() ?: 7878
            val soapUser = binding.etSoapUser.text.toString().trim()
            val soapPass = binding.etSoapPass.text.toString().trim()
            val emulator = binding.etEmulator.text.toString().trim().ifEmpty { "TrinityCore" }
            val version = binding.etGameVersion.text.toString().trim().ifEmpty { "WotLK" }
            lifecycleScope.launch {
                AppPrefs.saveSoapConfig(requireContext(), soapHost, soapPort, soapUser, soapPass, emulator, version)
                com.thundernet.admin.util.showSnack(view, "SOAP/Emulador guardado")
            }
        }

        binding.btnChangeAdminPass.setOnClickListener {
            val user = binding.etAdminUser.text.toString().trim()
            val pass = binding.etAdminPass.text.toString().trim()
            lifecycleScope.launch {
                val hash = sha256(pass)
                AppDatabase.get().adminUserDao().upsert(AdminUser(user, hash))
                com.thundernet.admin.util.showSnack(view, "Contraseña actualizada")
            }
        }
    }

    private fun sha256(input: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}