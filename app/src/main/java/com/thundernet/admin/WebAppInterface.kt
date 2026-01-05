package com.thundernet.admin

import android.content.Context
import android.content.Intent
import com.thundernet.web.WebActivity
import android.content.SharedPreferences
import android.webkit.JavascriptInterface
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement

class WebAppInterface(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("ThunderNetAdmin", Context.MODE_PRIVATE)
    private var connection: Connection? = null
    
    @JavascriptInterface
fun logout() {
    try {
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Error al cerrar sesión: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
    
    @JavascriptInterface
    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
    
    @JavascriptInterface
    fun getServerConfig(): String {
        return try {
            val config = JSONObject()
            config.put("ip", prefs.getString("server_ip", "localhost"))
            config.put("port", prefs.getString("server_port", "3306"))
            config.put("auth_db", prefs.getString("auth_db", "auth"))
            config.put("chars_db", prefs.getString("chars_db", "characters"))
            config.put("world_db", prefs.getString("world_db", "world"))
            config.put("soap_port", prefs.getString("soap_port", "7878"))
            config.put("ra_port", prefs.getString("ra_port", "3443"))
            config.toString()
        } catch (e: Exception) {
            "{\"error\": \"${e.message}\"}"
        }
    }
    
    @JavascriptInterface
fun saveServerConfig(config: String) {
    try {
        val json = JSONObject(config)
        val editor = prefs.edit()
        editor.putString("server_ip", json.optString("ip", "localhost"))
        editor.putString("server_port", json.optString("port", "3306"))
        editor.putString("auth_db", json.optString("auth_db", "auth"))
        editor.putString("chars_db", json.optString("chars_db", "characters"))
        editor.putString("world_db", json.optString("world_db", "world"))
        editor.putString("soap_port", json.optString("soap_port", "7878"))
        editor.putString("ra_port", json.optString("ra_port", "3443"))
        editor.apply()

        Toast.makeText(context, "Configuración guardada", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Error al guardar configuración: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
    
    @JavascriptInterface
    fun testDatabaseConnection(): String {
        return try {
            val ip = prefs.getString("server_ip", "localhost") ?: "localhost"
            val port = prefs.getString("server_port", "3306") ?: "3306"
            val url = "jdbc:mysql://$ip:$port/"
            
            Class.forName("com.mysql.jdbc.Driver")
            val conn = DriverManager.getConnection(url, "root", "root")
            
            val result = JSONObject()
            result.put("success", true)
            result.put("message", "Conexión exitosa")
            
            // Verificar bases de datos
            val stmt = conn.createStatement()
            val rs = stmt.executeQuery("SHOW DATABASES")
            val databases = JSONArray()
            while (rs.next()) {
                databases.put(rs.getString(1))
            }
            
            result.put("databases", databases)
            conn.close()
            result.toString()
        } catch (e: Exception) {
            val error = JSONObject()
            error.put("success", false)
            error.put("message", e.message ?: "Error desconocido")
            error.toString()
        }
    }
    
    @JavascriptInterface
    fun executeGMCommand(command: String): String {
        return executeQuery("auth", command)
    }
    
    @JavascriptInterface
    fun executeQuery(database: String, query: String): String {
        return try {
            val result = JSONObject()
            
            if (connection == null || connection!!.isClosed) {
                result.put("success", false)
                result.put("message", "Sin conexión a la base de datos")
                return result.toString()
            }
            
            val stmt = connection!!.createStatement()
            if (query.trim().uppercase().startsWith("SELECT")) {
                val rs = stmt.executeQuery(query)
                val results = JSONArray()
                val metaData = rs.metaData
                val columnCount = metaData.columnCount
                
                while (rs.next()) {
                    val row = JSONObject()
                    for (i in 1..columnCount) {
                        row.put(metaData.getColumnName(i), rs.getString(i) ?: "")
                    }
                    results.put(row)
                }
                
                result.put("success", true)
                result.put("data", results)
            } else {
                val affectedRows = stmt.executeUpdate(query)
                result.put("success", true)
                result.put("affectedRows", affectedRows)
            }
            
            result.toString()
        } catch (e: Exception) {
            val error = JSONObject()
            error.put("success", false)
            error.put("message", e.message ?: "Error desconocido")
            error.toString()
        }
    }
    
    @JavascriptInterface
    fun connectToDatabase(): String {
        return try {
            val ip = prefs.getString("server_ip", "localhost") ?: "localhost"
            val port = prefs.getString("server_port", "3306") ?: "3306"
            val authDb = prefs.getString("auth_db", "auth") ?: "auth"
            val url = "jdbc:mysql://$ip:$port/$authDb"
            
            Class.forName("com.mysql.jdbc.Driver")
            connection = DriverManager.getConnection(url, "root", "root")
            
            val result = JSONObject()
            result.put("success", true)
            result.put("message", "Conectado a $authDb")
            result.toString()
        } catch (e: Exception) {
            val error = JSONObject()
            error.put("success", false)
            error.put("message", e.message ?: "Error desconocido")
            error.toString()
        }
    }
    
    @JavascriptInterface
    fun disconnectDatabase() {
        try {
            connection?.close()
            connection = null
        } catch (e: Exception) {
            // Ignorar errores al desconectar
        }
    }
    
    @JavascriptInterface
    fun isConnected(): Boolean {
        return try {
            connection?.isValid(2) ?: false
        } catch (e: Exception) {
            false
        }
    }
}