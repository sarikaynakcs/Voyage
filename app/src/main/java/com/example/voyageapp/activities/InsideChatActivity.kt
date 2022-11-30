 package com.example.voyageapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
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
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

 class InsideChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInsideChatBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var messageAdapter: AdapterMessage
    private lateinit var messageList: ArrayList<ModelMessage>
    private lateinit var ref: DatabaseReference
    private lateinit var time: java.lang.StringBuilder
    private lateinit var date: java.lang.StringBuilder

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

        time = StringBuilder()
        date = StringBuilder()

        val today: Date = Calendar.getInstance().time
        val format_date: SimpleDateFormat = SimpleDateFormat("dd.MM.yyyy")
        val dateString: String = format_date.format(today)
        date.append(dateString)

        val timeOfDay: Date = Calendar.getInstance().time
        val format_time: SimpleDateFormat = SimpleDateFormat("hh:mm")
        val timeString: String = format_time.format(timeOfDay)
        time.append(timeString)


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


        val dbRef = FirebaseDatabase.getInstance().getReference("Blocklist")
        dbRef.child(receiverUid!!).child("blocklist")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChild(firebaseAuth.uid!!)) {
                        binding.status.visibility = View.GONE
                        binding.photoImg.setImageResource(R.drawable.ic_person_white)
                        binding.nameTxt.text = "Voyage Kullanıcısı"
                        binding.editChatMessage.isClickable = false
                        binding.buttonGchatSend.isClickable = false
                    } else {

                        dbRef.child(firebaseAuth.uid!!).child("blocklist")
                            .addValueEventListener(object : ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.hasChild(receiverUid)) {
                                        binding.status.visibility = View.GONE
                                        binding.photoImg.setImageResource(R.drawable.ic_person_white)
                                        binding.nameTxt.text = "Voyage Kullanıcısı"
                                        binding.editChatMessage.isClickable = false
                                        binding.buttonGchatSend.isClickable = false
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }

                            })
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
            val timestamp = System.currentTimeMillis()
            val messageObject = ModelMessage(message, senderUid, timestamp, time.toString(), date.toString())
            val databaseRef = FirebaseDatabase.getInstance().getReference("isChat")

            if (message.isNotEmpty()) {

                ref.child("Chats").child(senderRoom!!).child("Messages").push()
                    .setValue(messageObject)
                databaseRef.child(senderUid!!).child("chats").child(receiverUid).setValue(true)
                ref.child("Chats").child(receiverRoom!!).child("Messages").push()
                    .setValue(messageObject)
                databaseRef.child(receiverUid).child("chats").child(senderUid).setValue(true)

                messageBox.text.clear()
            }
        }

    }

    private fun checkStatus(receiverUid: String?) {
        val mRef = FirebaseDatabase.getInstance().getReference("Blocklist")
        val dbRef = FirebaseDatabase.getInstance().getReference("Status").child(receiverUid!!)

        mRef.child(receiverUid!!).child("blocklist")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChild(firebaseAuth.uid!!)) {
                        binding.status.visibility = View.GONE
                    } else {
                        mRef.child(firebaseAuth.uid!!).child("blocklist")
                            .addValueEventListener(object : ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.hasChild(receiverUid)) {
                                        binding.status.visibility = View.GONE
                                    } else {
                                        dbRef.child("status").addValueEventListener(object : ValueEventListener{
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                val status = snapshot.value.toString()

                                                if (status == "online") {
                                                    binding.status.visibility = View.VISIBLE
                                                }else {
                                                    binding.status.visibility = View.GONE
                                                }
                                            }

                                            override fun onCancelled(error: DatabaseError) {
                                                TODO("Not yet implemented")
                                            }

                                        })
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }

                            })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

    }

    private fun online(status: String) {
        val db = FirebaseDatabase.getInstance().getReference("Status").child(firebaseAuth.uid!!)
        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["status"] = status
        db.updateChildren(hashMap)
        val receiverUid = intent.getStringExtra("uid")
        checkStatus(receiverUid)
    }

    override fun onStart() {
        super.onStart()
        online("online")
    }

    /*override fun onStop() {
        super.onStop()
        online("offline")
    }*/

    override fun onPause() {
        super.onPause()
        online("offline")
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        //startActivity(Intent(this@InsideChatActivity, ChatActivity::class.java))
        //finish()
        overridePendingTransition(0,0)
    }
}