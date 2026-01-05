package com.thundenet.admin.data.repo

import android.content.Context
import kotlinx.coroutines.flow.first
import com.thundenet.admin.data.network.SoapClient
import com.thundenet.admin.data.network.SoapResult
import com.thundenet.admin.data.prefs.AppPrefs

class ServerRepository(private val context: Context) {

    @Volatile var isConnected: Boolean = false
    private suspend fun client(): SoapClient {
        val cfg = AppPrefs.soapConfig(context).first()
        return SoapClient(cfg.host, cfg.port, cfg.user, cfg.pass)
    }

    suspend fun testConnection(): Boolean {
        val c = client()
        val res = c.execute("server info")
        isConnected = res is SoapResult.Ok
        return isConnected
    }

    // --- Players / Accounts ---
    suspend fun banPlayer(account: String): Boolean {
        val c = client()
        // Ban permanente por cuenta
        val cmd = "ban account $account permanent"
        return c.execute(cmd) is SoapResult.Ok
    }

    suspend fun unbanPlayer(account: String): Boolean {
        val c = client()
        val cmd = "unban account $account"
        return c.execute(cmd) is SoapResult.Ok
    }

    // --- Items / Mail ---
    suspend fun giveItem(character: String, itemId: Int, count: Int): Boolean {
        val c = client()
        // Enviar por correo al personaje
        val subject = "ThundeNet Admin"
        val text = "Entrega de ítems"
        val cmd = "send items $character \"$subject\" \"$text\" $itemId:$count"
        return c.execute(cmd) is SoapResult.Ok
    }

    // --- Server ---
    suspend fun restartServer(): Boolean {
        val c = client()
        val cmd = "server restart 5"
        return c.execute(cmd) is SoapResult.Ok
    }

    // --- Commands (raw) ---
    suspend fun executeCommand(command: String): Boolean {
        val c = client()
        return c.execute(command) is SoapResult.Ok
    }

    // --- Broadcast ---
    suspend fun broadcast(message: String): Boolean {
        val c = client()
        val cmd = "announce $message"
        return c.execute(cmd) is SoapResult.Ok
    }

    // --- HomeStone / Unstuck + Teleport to home bind ---
    suspend fun homeStoneTeleport(characterName: String): Boolean {
        val c = client()
        // Destrabar y enviar a piedra hogar (bind location)
        val unstuck = c.execute("unstuck $characterName")
        val hearth = c.execute("teleport $characterName homebind")
        return (unstuck is SoapResult.Ok) && (hearth is SoapResult.Ok)
    }

    // --- Characters (examples) ---
    suspend fun resetTalents(characterName: String): Boolean {
        val c = client()
        val cmd = "reset talents $characterName"
        return c.execute(cmd) is SoapResult.Ok
    }

    suspend fun teleport(characterName: String, mapId: Int, x: Float, y: Float, z: Float): Boolean {
        val c = client()
        val cmd = "teleport $characterName $mapId $x $y $z"
        return c.execute(cmd) is SoapResult.Ok
    }

    // --- Events (generic) ---
    suspend fun startEvent(eventId: Int): Boolean {
        val c = client()
        val cmd = "event start $eventId"
        return c.execute(cmd) is SoapResult.Ok
    }

    suspend fun stopEvent(eventId: Int): Boolean {
        val c = client()
        val cmd = "event stop $eventId"
        return c.execute(cmd) is SoapResult.Ok
    }

    // --- Tickets ---
    suspend fun createTicket(characterName: String, text: String): Boolean {
        val c = client()
        val cmd = "ticket create $characterName \"$text\""
        return c.execute(cmd) is SoapResult.Ok
    }

    suspend fun closeTicket(ticketId: Int): Boolean {
        val c = client()
        val cmd = "ticket close $ticketId"
        return c.execute(cmd) is SoapResult.Ok
    }
}