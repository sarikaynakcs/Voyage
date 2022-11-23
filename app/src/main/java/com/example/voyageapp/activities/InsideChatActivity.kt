 package com.example.voyageapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.core.view.size
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.voyageapp.R
import com.example.voyageapp.adapters.AdapterMessage
import com.example.voyageapp.databinding.ActivityInsideChatBinding
import com.example.voyageapp.models.ModelMessage
import com.example.voyageapp.models.ModelUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class InsideChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInsideChatBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var messageAdapter: AdapterMessage
    private lateinit var messageList: ArrayList<ModelMessage>
    private lateinit var ref: DatabaseReference

    var receiverRoom: String? = null
    var senderRoom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInsideChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        firebaseAuth = FirebaseAuth.getInstance()

        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")

        binding.nameTxt.text = name

        val mRef = FirebaseDatabase.getInstance().getReference("Users")
        mRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds in snapshot.children){
                    val model = ds.getValue(ModelUser::class.java)
                    val profileImage = "${receiverUid?.let { snapshot.child(it).child("profileImage").value }}"

                    if (receiverUid == model?.uid) {
                        Glide.with(this@InsideChatActivity)
                            .load(profileImage)
                            .placeholder(R.drawable.ic_person_gray)
                            .into(binding.photoImg)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        //handle click back button
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        val senderUid = firebaseAuth.currentUser?.uid
        ref = FirebaseDatabase.getInstance().getReference()

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        chatRecyclerView = findViewById(R.id.inChatRv)
        messageBox = findViewById(R.id.edit_chat_message)
        sendButton = findViewById(R.id.button_gchat_send)
        messageList = ArrayList()
        messageAdapter = AdapterMessage(this@InsideChatActivity, messageList)
        chatRecyclerView.layoutManager = LinearLayoutManager(this@InsideChatActivity)
        chatRecyclerView.adapter = messageAdapter

        // logic for adding data to recyclerview
        ref.child("Chats").child(senderRoom!!).child("Messages")
            .addValueEventListener(object : ValueEventListener{
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()

                    for (ds in snapshot.children){
                        val message = ds.getValue(ModelMessage::class.java)
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()
                    chatRecyclerView.smoothScrollToPosition((chatRecyclerView.adapter as AdapterMessage).itemCount)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        // adding the message to database
        sendButton.setOnClickListener {
            val message = messageBox.text.toString()
            val messageObject = ModelMessage(message, senderUid)

            if (message.isNotEmpty()) {
                ref.child("Chats").child(senderRoom!!).child("Messages").push()
                    .setValue(messageObject).addOnSuccessListener {
                        ref.child("Chats").child(receiverRoom!!).child("Messages").push()
                            .setValue(messageObject)
                    }
                messageBox.text.clear()
            }
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        //super.onBackPressed()
        startActivity(Intent(this@InsideChatActivity, ChatActivity::class.java))
        finish()
        overridePendingTransition(0,0)
    }
}