package com.thundernet.admin.data.network

import okhttp3.Credentials
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

class SoapClient(
    private val host: String,
    private val port: Int,
    private val user: String,
    private val pass: String
) {
    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private val mediaType = "text/xml; charset=utf-8".toMediaType()

    private fun envelope(command: String): String = """
        <?xml version="1.0" encoding="UTF-8"?>
        <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ns1="urn:TC">
          <SOAP-ENV:Body>
            <ns1:executeCommand>
              <command>${command}</command>
            </ns1:executeCommand>
          </SOAP-ENV:Body>
        </SOAP-ENV:Envelope>
    """.trimIndent()

    fun execute): SoapResult {
        val url = "http://$host:$port/"
        val body = RequestBody.create(mediaType, envelope(command))
        val req = Request.Builder()
            .url(url)
            .post(body)
            .header("Authorization", Credentials.basic(user, pass))
            .header("Content-Type", "text/xml; charset=utf-8")
            .build()

        return try {
            client.newCall(req).execute().use { resp ->
                parseResponse(resp)
            }
        } catch (e: SocketTimeoutException) {
            SoapResult.Error("Timeout de conexión")
        } catch (e: Exception) {
            SoapResult.Error("Error de red: ${e.message}")
        }
    }

    private fun parseResponse(resp: Response): SoapResult {
        val code = resp.code
        val body = resp.body?.string().orEmpty()
        if (code in 200..299) {
            // TrinityCore devuelve el resultado dentro del body XML; aquí lo retornamos crudo.
            return SoapResult.Ok(body)
        }
        return SoapResult.Error("HTTP $code: $body")
    }
}

sealed class SoapResult {
    data class Ok(val raw: String) : SoapResult()
    data class Error(val message: String) : SoapResult()
}