package com.thunderia

import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.MotionEvent
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.thunderia.data.AppDatabase
import com.thunderia.data.Message
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import java.io.File
import java.io.IOException

class ChatActivity : AppCompatActivity() {

    private lateinit var adapter: MessageAdapter
    private val messages = mutableListOf<Message>()
    private lateinit var db: AppDatabase
    private val client = OkHttpClient()
    private val apiKey = "TU_API_KEY_DEEPSEEK" // reemplaza con tu clave

    private var mediaRecorder: MediaRecorder? = null
    private var audioFile: File? = null
    private val PICK_IMAGE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Inicializar Room
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "messages-db").build()

        // Configurar RecyclerView
        val recycler = findViewById<RecyclerView>(R.id.recyclerChat)
        adapter = MessageAdapter(messages)
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this)

        val input = findViewById<EditText>(R.id.inputText)
        val sendBtn = findViewById<ImageButton>(R.id.sendBtn)
        val audioBtn = findViewById<ImageButton>(R.id.audioBtn)
        val imageBtn = findViewById<ImageButton>(R.id.imageBtn)

        // Cargar historial desde Room
        GlobalScope.launch {
            val oldMessages = db.messageDao().getAll()
            runOnUiThread {
                messages.addAll(oldMessages)
                adapter.notifyDataSetChanged()
                recycler.scrollToPosition(messages.size - 1)
            }
        }

        // Enviar texto
        sendBtn.setOnClickListener {
            val userMsg = input.text.toString()
            if (userMsg.isNotEmpty()) {
                val msg = Message(text = userMsg, sender = "user", timestamp = System.currentTimeMillis())
                addMessage(msg)
                sendMessageToDeepSeek(userMsg)
                input.text.clear()
            }
        }

        // Audio (mantener presionado para grabar)
        audioBtn.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> startRecording()
                MotionEvent.ACTION_UP -> stopRecording()
            }
            true
        }

        // Selección de imagen
        imageBtn.setOnClickListener {
            openGallery()
        }
    }

    // Guardar mensaje en Room y mostrarlo
    private fun addMessage(message: Message) {
        GlobalScope.launch {
            db.messageDao().insert(message)
        }
        messages.add(message)
        adapter.notifyItemInserted(messages.size - 1)
    }

    // Conexión con DeepSeek
    private fun sendMessageToDeepSeek(message: String) {
        val json = """
        {
          "messages":[{"role":"user","content":"$message"}]
        }
        """.trimIndent()

        val request = Request.Builder()
            .url("https://api.deepseek.com/v1/chat/completions")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(RequestBody.create("application/json".toMediaType(), json))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                val reply = JSONObject(body ?: "")
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")

                runOnUiThread {
                    val msg = Message(text = reply, sender = "ai", timestamp = System.currentTimeMillis())
                    addMessage(msg)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
        })
    }

    // Grabación de audio
    private fun startRecording() {
        val dir = File(getExternalFilesDir(null), "audio")
        if (!dir.exists()) dir.mkdirs()
        audioFile = File(dir, "record_${System.currentTimeMillis()}.mp3")

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(audioFile!!.absolutePath)
            prepare()
            start()
        }
    }

    private fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null

        val msg = Message(audioPath = audioFile!!.absolutePath, sender = "user", timestamp = System.currentTimeMillis())
        addMessage(msg)
    }

    // Selección de imágenes
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            val imageUri = data?.data
            val msg = Message(imagePath = imageUri.toString(), sender = "user", timestamp = System.currentTimeMillis())
            addMessage(msg)
        }
    }
}