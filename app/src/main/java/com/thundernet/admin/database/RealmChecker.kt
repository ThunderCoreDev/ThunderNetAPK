package com.thundernet.admin.database

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import java.net.HttpURLConnection
import java.net.URL
import java.sql.Connection
import java.sql.DriverManager
import kotlin.concurrent.thread

class RealmChecker(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences("ThunderNetAdmin", Context.MODE_PRIVATE)
    
    fun checkRealms(): List<RealmStatus> {
        val realms = mutableListOf<RealmStatus>()
        
        try {
            val ip = prefs.getString("server_ip", "localhost") ?: "localhost"
            val authDb = prefs.getString("auth_db", "auth") ?: "auth"
            val username = prefs.getString("db_username", "root") ?: "root"
            val password = prefs.getString("db_password", "") ?: ""
            
            val url = "jdbc:mysql://$ip:3306/$authDb"
            Class.forName("com.mysql.jdbc.Driver")
            val conn: Connection = DriverManager.getConnection(url, username, password)
            
            val stmt = conn.createStatement()
            val rs = stmt.executeQuery("SELECT id, name, address, port, icon, flag, timezone, allowedSecurityLevel, population, gamebuild FROM realmlist")
            
            while (rs.next()) {
                val realm = RealmStatus(
                    id = rs.getInt("id"),
                    name = rs.getString("name"),
                    address = rs.getString("address"),
                    port = rs.getInt("port"),
                    icon = rs.getInt("icon"),
                    flag = rs.getInt("flag"),
                    timezone = rs.getInt("timezone"),
                    allowedSecurityLevel = rs.getInt("allowedSecurityLevel"),
                    population = rs.getFloat("population"),
                    gamebuild = rs.getInt("gamebuild"),
                    online = checkRealmOnline(rs.getString("address"), rs.getInt("port"))
                )
                realms.add(realm)
            }
            
            rs.close()
            stmt.close()
            conn.close()
            
        } catch (e: Exception) {
            Log.e("RealmChecker", "Error checking realms: ${e.message}")
        }
        
        return realms
    }
    
    private fun checkRealmOnline(address: String, port: Int): Boolean {
        return try {
            val url = URL("http://$address:$port")
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 3000
            connection.readTimeout = 3000
            connection.requestMethod = "HEAD"
            val responseCode = connection.responseCode
            connection.disconnect()
            responseCode == 200
        } catch (e: Exception) {
            false
        }
    }
    
    data class RealmStatus(
        val id: Int,
        val name: String,
        val address: String,
        val port: Int,
        val icon: Int,
        val flag: Int,
        val timezone: Int,
        val allowedSecurityLevel: Int,
        val population: Float,
        val gamebuild: Int,
        val online: Boolean
    )
}