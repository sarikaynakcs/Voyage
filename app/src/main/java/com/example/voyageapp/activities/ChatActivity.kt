package com.example.voyageapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.example.voyageapp.R
import com.example.voyageapp.adapters.AdapterUser
import com.example.voyageapp.databinding.ActivityChatBinding
import com.example.voyageapp.models.ModelUser
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatActivity : AppCompatActivity() {

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
        loadUsers()

        navigationViewTop = findViewById(R.id.linearTopIdChat)
        navigationViewTop.selectedItemId = R.id.nav_chats
        // Perform item seleceted listener

        navigationViewTop.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_chats -> return@OnNavigationItemSelectedListener true

                R.id.nav_friends -> {
                    startActivity(Intent(applicationContext, FriendsActivity::class.java))
                    finish()
                    overridePendingTransition(0,0)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_add -> {
                    startActivity(Intent(applicationContext, AddFriendsActivity::class.java))
                    finish()
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
                    finish()
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_notification -> {
                    startActivity(Intent(applicationContext, NotificationActivity::class.java))
                    finish()
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_game -> {
                    startActivity(Intent(applicationContext, GameActivity::class.java))
                    finish()
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(applicationContext, ProfileActivity::class.java))
                    finish()
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }

    private fun loadUsers() {
        //init arraylist
        userList = ArrayList()

        //get all users from firebase database...
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                //clear list before starting adding data into it
                userList.clear()
                for (ds in snapshot.children){
                    //get data as model
                    val model = ds.getValue(ModelUser::class.java)
                    val userType = ds.child("userType").getValue(String::class.java)
                    val uid = ds.child("uid").getValue(String::class.java)
                    if (userType != "admin" && uid != firebaseAuth.currentUser?.uid){
                        userList.add(model!!)
                    }
                }
                //setup adapter
                adapterUser = AdapterUser(this@ChatActivity, userList)
                //set adapter to recyclerview
                binding.chatsRv.adapter = adapterUser
                adapterUser.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        startActivity(Intent(this@ChatActivity, DashboardUserActivity::class.java))
        overridePendingTransition(0,0)
        finish()
    }
}