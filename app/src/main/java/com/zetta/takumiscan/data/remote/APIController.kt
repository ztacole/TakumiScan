package com.zetta.takumiscan.data.remote

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.DataOutputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class APIController(url: String, method: String) {
    private var conn: HttpURLConnection = URL(url).openConnection() as HttpURLConnection

    init {
        conn.requestMethod = method
        conn.doOutput = true
    }

    fun execute(postData: Map<String, Any>? = null, onResponse: (String, Int)-> Unit) {
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                postData?.let { data ->
                    val postDataString = data.entries.joinToString("&") { "${it.key}=${it.value}" }
                    conn.outputStream.use { output ->
                        OutputStreamWriter(output).use { writer ->
                            writer.write(postDataString)
                            writer.flush()
                        }
                    }
                }
                val response = conn.inputStream.bufferedReader().use { it.readText() }
                withContext(Dispatchers.Main) {
                    onResponse(response, conn.responseCode)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResponse("Error: ${e.message}", conn.responseCode)
                }
            }
        }
    }
}