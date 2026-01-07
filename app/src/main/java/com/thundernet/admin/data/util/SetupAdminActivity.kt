package com.thundernet.admin.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.thundernet.admin.data.db.AdminUser
import com.thundernet.admin.data.db.AppDatabase
import com.thundernet.admin.data.prefs.AppPrefs
import com.thundernet.admin.databinding.ActivitySetupAdminBinding
import kotlinx.coroutines.launch
import java.security.MessageDigest

class SetupAdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySetupAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            AppPrefs.setupDone(this@SetupAdminActivity).collect { done ->
                if (done) {
                    startActivity(Intent(this@SetupAdminActivity, LoginActivity::class.java))
                    finish()
                }
            }
        }

        binding.btnSave.setOnClickListener {
            val user = binding.etUser.text.toString().trim()
            val pass = binding.etPass.text.toString().trim()
            if (user.isEmpty() || pass.isEmpty()) {
                binding.etUser.error = "Requerido"
                binding.etPass.error = "Requerido"
                return@setOnClickListener
            }
            lifecycleScope.launch {
                val hash = sha256(pass)
                AppDatabase.get().adminUserDao().upsert(AdminUser(user, hash))
                AppPrefs.setSetupDone(this@SetupAdminActivity, true)
                startActivity(Intent(this@SetupAdminActivity, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun sha256(input: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}