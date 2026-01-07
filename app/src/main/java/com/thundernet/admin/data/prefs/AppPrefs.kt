package com.thundernet.admin.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "thundenet_prefs")

object AppPrefs {
    private val KEY_SETUP_DONE = booleanPreferencesKey("setup_done")
    private val KEY_REMEMBER_SESSION = booleanPreferencesKey("remember_session")

    // DB config
    private val KEY_DB_HOST = stringPreferencesKey("db_host")
    private val KEY_DB_PORT = intPreferencesKey("db_port")
    private val KEY_DB_USER = stringPreferencesKey("db_user")
    private val KEY_DB_PASS = stringPreferencesKey("db_pass")

    // SOAP config
    private val KEY_SOAP_HOST = stringPreferencesKey("soap_host")
    private val KEY_SOAP_PORT = intPreferencesKey("soap_port")
    private val KEY_SOAP_USER = stringPreferencesKey("soap_user")
    private val KEY_SOAP_PASS = stringPreferencesKey("soap_pass")
    private val KEY_EMULATOR = stringPreferencesKey("emulator")
    private val KEY_GAME_VERSION = stringPreferencesKey("game_version")

    fun setupDone(context: Context): Flow<Boolean> =
        context.dataStore.data.map { it[KEY_SETUP_DONE] ?: false }

    suspend fun setSetupDone(context: Context, value: Boolean) {
        context.dataStore.edit { it[KEY_SETUP_DONE] = value }
    }

    fun rememberSession(context: Context): Flow<Boolean> =
        context.dataStore.data.map { it[KEY_REMEMBER_SESSION] ?: true }

    suspend fun setRememberSession(context: Context, value: Boolean) {
        context.dataStore.edit { it[KEY_REMEMBER_SESSION] = value }
    }

    suspend fun saveDbConfig(
        context: Context,
        host: String,
        port: Int,
        user: String,
        pass: String
    ) {
        context.dataStore.edit {
            it[KEY_DB_HOST] = host
            it[KEY_DB_PORT] = port
            it[KEY_DB_USER] = user
            it[KEY_DB_PASS] = pass
        }
    }

    suspend fun saveSoapConfig(
        context: Context,
        host: String,
        port: Int,
        user: String,
        pass: String,
        emulator: String,
        gameVersion: String
    ) {
        context.dataStore.edit {
            it[KEY_SOAP_HOST] = host
            it[KEY_SOAP_PORT] = port
            it[KEY_SOAP_USER] = user
            it[KEY_SOAP_PASS] = pass
            it[KEY_EMULATOR] = emulator
            it[KEY_GAME_VERSION] = gameVersion
        }
    }

    // Getters (Flow) para uso en repositorio
    fun soapConfig(context: Context): Flow<SoapConfig> =
        context.dataStore.data.map {
            SoapConfig(
                host = it[KEY_SOAP_HOST] ?: "",
                port = it[KEY_SOAP_PORT] ?: 7878,
                user = it[KEY_SOAP_USER] ?: "",
                pass = it[KEY_SOAP_PASS] ?: "",
                emulator = it[KEY_EMULATOR] ?: "TrinityCore",
                gameVersion = it[KEY_GAME_VERSION] ?: "WotLK"
            )
        }
}

data class SoapConfig(
    val host: String,
    val port: Int,
    val user: String,
    val pass: String,
    val emulator: String,
    val gameVersion: String
)