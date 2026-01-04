package com.thundernet.admin.database

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement

class MySQLHelper(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences("ThunderNetAdmin", Context.MODE_PRIVATE)
    private var connection: Connection? = null
    
    init {
        try {
            Class.forName("com.mysql.jdbc.Driver")
        } catch (e: ClassNotFoundException) {
            Log.e("MySQLHelper", "Driver JDBC no encontrado", e)
        }
    }
    
    fun connect(): Boolean {
        return try {
            val ip = prefs.getString("server_ip", "localhost") ?: "localhost"
            val port = prefs.getString("server_port", "3306") ?: "3306"
            val authDb = prefs.getString("auth_db", "auth") ?: "auth"
            val username = prefs.getString("db_username", "root") ?: "root"
            val password = prefs.getString("db_password", "") ?: ""
            
            val url = "jdbc:mysql://$ip:$port/$authDb"
            connection = DriverManager.getConnection(url, username, password)
            true
        } catch (e: Exception) {
            Log.e("MySQLHelper", "Error de conexión: ${e.message}")
            false
        }
    }
    
    fun disconnect() {
        try {
            connection?.close()
            connection = null
        } catch (e: Exception) {
            Log.e("MySQLHelper", "Error al desconectar: ${e.message}")
        }
    }
    
    fun isConnected(): Boolean {
        return try {
            connection?.isValid(2) ?: false
        } catch (e: Exception) {
            false
        }
    }
    
    fun executeQuery(query: String): ResultSet? {
        return try {
            if (!isConnected() && !connect()) {
                return null
            }
            val stmt: Statement = connection!!.createStatement()
            stmt.executeQuery(query)
        } catch (e: Exception) {
            Log.e("MySQLHelper", "Error en query: ${e.message}")
            null
        }
    }
    
    fun executeUpdate(query: String): Int {
        return try {
            if (!isConnected() && !connect()) {
                return -1
            }
            val stmt: Statement = connection!!.createStatement()
            stmt.executeUpdate(query)
        } catch (e: Exception) {
            Log.e("MySQLHelper", "Error en update: ${e.message}")
            -1
        }
    }
    
    fun getAccounts(): List<Map<String, Any>> {
        val accounts = mutableListOf<Map<String, Any>>()
        try {
            val query = "SELECT id, username, email, gmlevel, last_ip, last_login FROM account LIMIT 100"
            val rs = executeQuery(query)
            
            rs?.let {
                while (rs.next()) {
                    val account = mapOf(
                        "id" to rs.getInt("id"),
                        "username" to rs.getString("username"),
                        "email" to rs.getString("email"),
                        "gmlevel" to rs.getInt("gmlevel"),
                        "last_ip" to rs.getString("last_ip"),
                        "last_login" to rs.getTimestamp("last_login").toString()
                    )
                    accounts.add(account)
                }
                rs.close()
            }
        } catch (e: Exception) {
            Log.e("MySQLHelper", "Error obteniendo cuentas: ${e.message}")
        }
        return accounts
    }
    
    fun getOnlinePlayers(): List<Map<String, Any>> {
        val players = mutableListOf<Map<String, Any>>()
        try {
            val query = """
                SELECT guid, name, race, class, level, zone, map, online 
                FROM characters 
                WHERE online = 1 
                LIMIT 50
            """.trimIndent()
            
            val rs = executeQuery(query)
            
            rs?.let {
                while (rs.next()) {
                    val player = mapOf(
                        "guid" to rs.getInt("guid"),
                        "name" to rs.getString("name"),
                        "race" to rs.getInt("race"),
                        "class" to rs.getInt("class"),
                        "level" to rs.getInt("level"),
                        "zone" to rs.getInt("zone"),
                        "map" to rs.getInt("map"),
                        "online" to rs.getInt("online")
                    )
                    players.add(player)
                }
                rs.close()
            }
        } catch (e: Exception) {
            Log.e("MySQLHelper", "Error obteniendo jugadores: ${e.message}")
        }
        return players
    }
}