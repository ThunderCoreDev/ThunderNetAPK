package com.thundenet.admin.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.thundenet.admin.MainActivity
import com.thundenet.admin.data.db.AppDatabase
import com.thundenet.admin.data.prefs.AppPrefs
import com.thundenet.admin.databinding.ActivityLoginBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.security.MessageDigest

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            val remember = AppPrefs.rememberSession(this@LoginActivity).first()
            if (remember) {
                // En un caso real, validar token/sesión. Aquí seguimos al login.
            }
        }

        binding.btnLogin.setOnClickListener {
            val user = binding.etUser.text.toString().trim()
            val pass = binding.etPass.text.toString().trim()
            lifecycleScope.launch {
                val dao = AppDatabase.get().adminUserDao()
                val admin = dao.get(user)
                val ok = admin != null && admin.passwordHash == sha256(pass)
                if (ok) {
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    binding.etPass.error = "Credenciales inválidas"
                }
            }
        }
    }

    private fun sha256(input: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}