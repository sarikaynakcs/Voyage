package com.example.voyageapp.adapters

import android.app.AlertDialog
import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.voyageapp.R
import com.example.voyageapp.databinding.DeleteLayoutBinding
import com.example.voyageapp.databinding.DeleteLayoutReceiveBinding
import com.example.voyageapp.models.ModelMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AdapterMessage(val context: Context,
                     messageList: ArrayList<ModelMessage>?,
                     senderRoom: String,
                     receiverRoom: String
):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var messageList: ArrayList<ModelMessage>
    val ITEM_RECEIVE = 1
    val ITEM_SENT = 2
    val senderRoom :String
    val receiverRoom:String


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        if (viewType == 1){
            //inflate receive
            val view: View = LayoutInflater.from(context).inflate(R.layout.received, parent, false)
            return ReceivedViewHolder(view)
        }
        else{
            //inflate sent
            val view: View = LayoutInflater.from(context).inflate(R.layout.sent, parent, false)
            return SentViewHolder(view)
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentMessage = messageList[position]

        if (holder.javaClass == SentViewHolder::class.java) {
            //do the stuff for sent view holder
            val viewHolder = holder as SentViewHolder

            viewHolder.sentMessage.text = currentMessage.message
            viewHolder.sentDate.text = currentMessage.date
            viewHolder.sentTime.text = currentMessage.time

            viewHolder.itemView.setOnLongClickListener {

                val view = LayoutInflater.from(context).inflate(R.layout.delete_layout,null)
                val binding:DeleteLayoutBinding = DeleteLayoutBinding.bind(view)
                val dialog = AlertDialog.Builder(context)
                    .setTitle("Mesaj silinsin mi?")
                    .setView(binding.root)
                    .create()
                binding.everyone.setOnClickListener {
                    currentMessage.message = "Bu mesaj silindi"
                    currentMessage.messageId?.let { it1->
                    FirebaseDatabase.getInstance().reference.child("Chats")
                        .child(senderRoom)
                        .child("Messages")
                        .child(it1).setValue(currentMessage)

                    }
                    currentMessage.messageId?.let { it1->
                        FirebaseDatabase.getInstance().reference.child("Chats")
                            .child(receiverRoom)
                            .child("Messages")
                            .child(it1).setValue(currentMessage)
                    }
                    dialog.dismiss()
                }
                binding.delete.setOnClickListener {
                    currentMessage.messageId?.let { it1->
                        FirebaseDatabase.getInstance().reference.child("Chats")
                            .child(senderRoom)
                            .child("Messages")
                            .child(it1).setValue(null)

                    }
                    dialog.dismiss()
                }
                binding.cancel.setOnClickListener { dialog.dismiss() }
                dialog.show()
                false
            }

        }
        else{
            //do the stuff for receive view holder
            val viewHolder = holder as ReceivedViewHolder

            viewHolder.receivedMessage.text = currentMessage.message
            viewHolder.receivedDate.text = currentMessage.date
            viewHolder.receivedTime.text = currentMessage.time

            viewHolder.itemView.setOnLongClickListener {

                val view = LayoutInflater.from(context).inflate(R.layout.delete_layout_receive,null)
                val binding:DeleteLayoutReceiveBinding = DeleteLayoutReceiveBinding.bind(view)
                val dialog = AlertDialog.Builder(context)
                    .setTitle("Mesaj silinsin mi?")
                    .setView(binding.root)
                    .create()
                binding.deleteReceive.setOnClickListener {
                    currentMessage.messageId?.let { it1->
                        FirebaseDatabase.getInstance().reference.child("Chats")
                            .child(senderRoom)
                            .child("Messages")
                            .child(it1).setValue(null)

                    }
                    dialog.dismiss()
                }
                binding.cancelReceive.setOnClickListener { dialog.dismiss() }
                dialog.show()
                false
            }
        }
    }

    override fun getItemViewType(position: Int): Int {

        val currentMessage = messageList[position]

        if (FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)){
            return ITEM_SENT
        }
        else{
            return ITEM_RECEIVE
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sentMessage = itemView.findViewById<TextView>(R.id.text_gchat_message_me)
        val sentDate = itemView.findViewById<TextView>(R.id.message_date_sent)
        val sentTime = itemView.findViewById<TextView>(R.id.message_time_sent)

    }

    class ReceivedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receivedMessage = itemView.findViewById<TextView>(R.id.text_gchat_message_other)
        val receivedDate = itemView.findViewById<TextView>(R.id.message_date_received)
        val receivedTime = itemView.findViewById<TextView>(R.id.message_time_received)
    }

    init {
        if (messageList != null) {
            this.messageList = messageList
        }
        this.senderRoom = senderRoom
        this.receiverRoom = receiverRoom
    }
}