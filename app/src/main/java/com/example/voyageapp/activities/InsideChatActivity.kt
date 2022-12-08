 package com.example.voyageapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
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
    var database: FirebaseDatabase? = null

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
        database = FirebaseDatabase.getInstance()

        time = StringBuilder()
        date = StringBuilder()

        val today: Date = Calendar.getInstance().time
        val format_date: SimpleDateFormat = SimpleDateFormat("dd.MM.yyyy")
        val dateString: String = format_date.format(today)
        date.append(dateString)

        val timeOfDay: Date = Calendar.getInstance().time
        val format_time: SimpleDateFormat = SimpleDateFormat("HH:mm")
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

        val handler = Handler()
        binding.editChatMessage.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                online("yazıyor...")
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed(userStoppedTyping, 1000)
            }
            var userStoppedTyping = Runnable {
                online("çevrimiçi")
            }
        })

        //handle click back button
        binding.backBtn.setOnClickListener {
            val databaseRef = FirebaseDatabase.getInstance().getReference("isChat")
            databaseRef.child(firebaseAuth.uid!!).child("chats")
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.child(receiverUid!!).exists()) {
                            databaseRef.child(firebaseAuth.uid!!).child("chats").child(receiverUid).setValue(false)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            startActivity(Intent(this@InsideChatActivity, ChatActivity::class.java))
            finish()
            overridePendingTransition(0,0)
        }

        val senderUid = firebaseAuth.currentUser?.uid
        ref = FirebaseDatabase.getInstance().getReference()

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        chatRecyclerView = findViewById(R.id.inChatRv)
        messageBox = findViewById(R.id.edit_chat_message)
        sendButton = findViewById(R.id.button_gchat_send)
        messageList = ArrayList()
        messageAdapter = AdapterMessage(this@InsideChatActivity, messageList,senderRoom!!,receiverRoom!!)
        chatRecyclerView.layoutManager = LinearLayoutManager(this@InsideChatActivity)
        chatRecyclerView.adapter = messageAdapter

        val databaseRef = FirebaseDatabase.getInstance().getReference("isChat")
        databaseRef.child(senderUid!!).child("chats")
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child(receiverUid).exists()) {
                        databaseRef.child(senderUid!!).child("chats").child(receiverUid).setValue(true)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        // logic for adding data to recyclerview
        ref.child("Chats").child(senderRoom!!).child("Messages")
            .addValueEventListener(object : ValueEventListener{
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()

                    for (ds in snapshot.children){
                        val message = ds.getValue(ModelMessage::class.java)
                        message!!.messageId = ds.key
                        messageList.add(message)
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
            val randomKey = database!!.reference.push().key
            val lastMsgObj = HashMap<String,Any>()
            lastMsgObj["date"] = date.toString()
            lastMsgObj["message"] = messageObject.message!!
            lastMsgObj["senderId"] = messageObject.senderId!!
            lastMsgObj["time"] = time.toString()
            lastMsgObj["timestamp"] = messageObject.timestamp
            val databaseRef = FirebaseDatabase.getInstance().getReference("isChat")

            if (message.isNotEmpty()) {

                database!!.reference.child("Chats").child(senderRoom!!)
                    .updateChildren(lastMsgObj)
                database!!.reference.child("Chats").child(receiverRoom!!)
                    .updateChildren(lastMsgObj)
                database!!.reference.child("Chats").child(senderRoom!!)
                    .child("Messages").child(randomKey!!)
                    .setValue(messageObject).addOnSuccessListener {
                        database!!.reference.child("Chats").child(receiverRoom!!)
                            .child("Messages").child(randomKey)
                            .setValue(messageObject)
                            .addOnSuccessListener {
                                database!!.reference.child("isChat").child(senderUid!!).child("chats")
                                    .addListenerForSingleValueEvent(object : ValueEventListener{
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (!snapshot.child(receiverUid).exists()) {
                                                databaseRef.child(senderUid!!).child("chats").child(receiverUid).setValue(true)
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            TODO("Not yet implemented")
                                        }

                                    })
                                database!!.reference.child("isChat").child(receiverUid).child("chats")
                                    .addListenerForSingleValueEvent(object : ValueEventListener{
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (!snapshot.child(senderUid).exists()) {
                                                databaseRef.child(receiverUid).child("chats").child(senderUid).setValue(false)
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            TODO("Not yet implemented")
                                        }

                                    })
                            }
                    }

                messageBox.text.clear()
            }
        }

    }

     private fun checkStatus(receiverUid: String?) {
         val mRef = FirebaseDatabase.getInstance().getReference("Blocklist")
         val dbRef = FirebaseDatabase.getInstance().getReference("Status").child(receiverUid!!)
         val databaseRef = FirebaseDatabase.getInstance().getReference("isChat")

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
                                         databaseRef.child(receiverUid).child("chats").child(firebaseAuth.uid!!)
                                             .addValueEventListener(object : ValueEventListener{
                                                 override fun onDataChange(snapshot: DataSnapshot) {
                                                     val isChat = snapshot.value.toString()

                                                     if (isChat == "true") {
                                                         dbRef.child("status").addValueEventListener(object : ValueEventListener{
                                                             override fun onDataChange(snapshot: DataSnapshot) {
                                                                 val status = snapshot.value.toString()

                                                                 if (status != "null") {
                                                                     binding.status.text = status
                                                                 }
                                                             }

                                                             override fun onCancelled(error: DatabaseError) {
                                                                 TODO("Not yet implemented")
                                                             }

                                                         })
                                                     } else {
                                                         dbRef.child("status").addValueEventListener(object : ValueEventListener{
                                                             override fun onDataChange(snapshot: DataSnapshot) {
                                                                 val status = snapshot.value.toString()

                                                                 if (status != "null") {
                                                                     if (status == "yazıyor...") {
                                                                         binding.status.text = "çevrimiçi"
                                                                     } else {
                                                                         binding.status.text = status
                                                                     }
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
         online("çevrimiçi")
         val receiverUid = intent.getStringExtra("uid")
         val databaseRef = FirebaseDatabase.getInstance().getReference("isChat")
         databaseRef.child(firebaseAuth.uid!!).child("chats")
             .addListenerForSingleValueEvent(object : ValueEventListener{
                 override fun onDataChange(snapshot: DataSnapshot) {
                     if (snapshot.child(receiverUid!!).exists()) {
                         databaseRef.child(firebaseAuth.uid!!).child("chats").child(receiverUid).setValue(true)
                     }
                 }

                 override fun onCancelled(error: DatabaseError) {
                     TODO("Not yet implemented")
                 }

             })
     }

     override fun onPause() {
         super.onPause()
         val time = time.toString()
         val date = date.toString()
         val lastSeen = date + ", " + time
         online("Son görülme $lastSeen")
         val receiverUid = intent.getStringExtra("uid")
         val databaseRef = FirebaseDatabase.getInstance().getReference("isChat")
         databaseRef.child(firebaseAuth.uid!!).child("chats")
             .addListenerForSingleValueEvent(object : ValueEventListener{
                 override fun onDataChange(snapshot: DataSnapshot) {
                     if (snapshot.child(receiverUid!!).exists()) {
                         databaseRef.child(firebaseAuth.uid!!).child("chats").child(receiverUid).setValue(false)
                     }
                 }

                 override fun onCancelled(error: DatabaseError) {
                     TODO("Not yet implemented")
                 }

             })
     }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val receiverUid = intent.getStringExtra("uid")
        val databaseRef = FirebaseDatabase.getInstance().getReference("isChat")
        databaseRef.child(firebaseAuth.uid!!).child("chats")
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child(receiverUid!!).exists()) {
                        databaseRef.child(firebaseAuth.uid!!).child("chats").child(receiverUid).setValue(false)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        startActivity(Intent(this@InsideChatActivity, ChatActivity::class.java))
        finish()
        overridePendingTransition(0,0)
    }
}