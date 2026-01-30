package com.thunderia

import android.media.MediaPlayer
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.thunderia.data.Message

class MessageAdapter(private val messages: List<Message>) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].sender == "user") 0 else 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layout = if (viewType == 0) R.layout.item_message_user else R.layout.item_message_ai
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int = messages.size

    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textMessage: TextView? = view.findViewById(R.id.textMessage)
        private val imageMessage: ImageView? = view.findViewById(R.id.imageMessage)
        private val audioMessage: Button? = view.findViewById(R.id.audioMessage)

        fun bind(message: Message) {
            // Reset visibilidad
            textMessage?.visibility = View.GONE
            imageMessage?.visibility = View.GONE
            audioMessage?.visibility = View.GONE

            when {
                message.text != null -> {
                    textMessage?.text = message.text
                    textMessage?.visibility = View.VISIBLE
                }
                message.imagePath != null -> {
                    imageMessage?.setImageURI(Uri.parse(message.imagePath))
                    imageMessage?.visibility = View.VISIBLE
                }
                message.audioPath != null -> {
                    audioMessage?.visibility = View.VISIBLE
                    audioMessage?.setOnClickListener {
                        val player = MediaPlayer().apply {
                            setDataSource(message.audioPath)
                            prepare()
                            start()
                        }
                    }
                }
            }
        }
    }
}