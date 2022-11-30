package com.example.voyageapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.voyageapp.R
import com.example.voyageapp.adapters.AdapterUser
import com.example.voyageapp.databinding.ActivityChatBinding
import com.example.voyageapp.models.ModelUser
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_profile.*

class ChatActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var binding: ActivityChatBinding

    private lateinit var navigationView: BottomNavigationView

    private lateinit var navigationViewTop: BottomNavigationView

    private lateinit var adapterUser: AdapterUser

    private lateinit var userList: ArrayList<ModelUser>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        //init firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()
        //init arraylist
        userList = ArrayList()
        //setup adapter
        adapterUser = AdapterUser(this@ChatActivity, userList)
        //set adapter to recyclerview
        binding.chatsRv.layoutManager = LinearLayoutManager(this)
        binding.chatsRv.adapter = adapterUser
        loadUsers()
        loadUserInfo()

        binding.apply {
            toggle= ActionBarDrawerToggle(this@ChatActivity,drawerLayoutChat,R.string.open,R.string.close)
            drawerLayoutChat.addDrawerListener(toggle)
            toggle.syncState()

            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            navViewChat.itemIconTintList=null

            navViewChat.setNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.firstItem->{
                        val intent=Intent(this@ChatActivity,ProfileUpdateActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(0,0)
                    }
                    R.id.secondItem->{
                        val intent=Intent(this@ChatActivity,BlockListActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(0,0)
                    }
                    R.id.thirdItem->{
                        firebaseAuth.signOut()
                        val intent = Intent(this@ChatActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        overridePendingTransition(0,0)
                    }
                }
                true
            }
        }

        navigationViewTop = findViewById(R.id.linearTopIdChat)
        navigationViewTop.selectedItemId = R.id.nav_chats
        // Perform item seleceted listener

        navigationViewTop.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_chats -> return@OnNavigationItemSelectedListener true

                R.id.nav_friends -> {
                    startActivity(Intent(applicationContext, FriendsActivity::class.java))
                    //finish()
                    overridePendingTransition(0,0)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_add -> {
                    startActivity(Intent(applicationContext, AddFriendsActivity::class.java))
                    //finish()
                    overridePendingTransition(0,0)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })

        navigationView = findViewById(R.id.linearBotIdChat)
        navigationView.selectedItemId = R.id.nav_chat
        // Perform item selected listener
        navigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_chat -> return@OnNavigationItemSelectedListener true

                R.id.nav_information -> {
                    startActivity(Intent(applicationContext, DashboardUserActivity::class.java))
                    //finish()
                    finishAffinity()
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_game -> {
                    startActivity(Intent(applicationContext, GameActivity::class.java))
                    //finish()
                    finishAffinity()
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(applicationContext, ProfileActivity::class.java))
                    //finish()
                    finishAffinity()
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }

    private fun loadUserInfo() {
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!)
            .addValueEventListener(object: ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {
                    val email = "${snapshot.child("email").value}"
                    val name = "${snapshot.child("name").value}"
                    val barcode = "${snapshot.child("barcodeId").value}"
                    val profileImage = "${snapshot.child("profileImage").value}"

                    findViewById<TextView>(R.id.nameHeader).text = name
                    findViewById<TextView>(R.id.emailHeader).text = email

                    Glide.with(this@ChatActivity)
                        .load(profileImage)
                        .placeholder(R.drawable.ic_person_gray)
                        .into(findViewById(R.id.profileImageHeader))

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun loadUsers() {

        //get all users from firebase database...
        val mRef = FirebaseDatabase.getInstance().getReference("isChat")
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                //clear list before starting adding data into it
                userList.clear()
                for (ds in snapshot.children){
                    //get data as model
                    val model = ds.getValue(ModelUser::class.java)
                    val uid = ds.child("uid").getValue(String::class.java)
                    val senderRoom = uid + firebaseAuth.currentUser?.uid

                    mRef.child(firebaseAuth.uid!!).child("chats")
                        .addChildEventListener(object : ChildEventListener{
                            override fun onChildAdded(
                                snapshot: DataSnapshot,
                                previousChildName: String?
                            ) {
                                if (snapshot.key == model!!.uid) {
                                    if (!userList.contains(model)) {
                                        userList.add(model)
                                        adapterUser.notifyDataSetChanged()
                                    }
                                }
                            }

                            override fun onChildChanged(
                                snapshot: DataSnapshot,
                                previousChildName: String?
                            ) {
                                TODO("Not yet implemented")
                            }

                            override fun onChildRemoved(snapshot: DataSnapshot) {
                                if (!snapshot.hasChild(model!!.uid)) {
                                    //userList.remove(model)
                                    adapterUser.notifyDataSetChanged()
                                }
                            }

                            override fun onChildMoved(
                                snapshot: DataSnapshot,
                                previousChildName: String?
                            ) {
                                TODO("Not yet implemented")
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                        })
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun online(status: String) {
        val db = FirebaseDatabase.getInstance().getReference("Status").child(firebaseAuth.uid!!)
        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["status"] = status
        db.updateChildren(hashMap)
    }

    override fun onStart() {
        super.onStart()
        online("online")
    }

    override fun onResume() {
        super.onResume()
        online("online")
    }

    override fun onPause() {
        super.onPause()
        online("offline")
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        online("offline")
        startActivity(Intent(this@ChatActivity, DashboardUserActivity::class.java))
        overridePendingTransition(0,0)
        finishAffinity()
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            true
        }
        return super.onOptionsItemSelected(item)
    }
}