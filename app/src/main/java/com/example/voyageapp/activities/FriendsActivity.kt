package com.example.voyageapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voyageapp.R
import com.example.voyageapp.adapters.AdapterUser
import com.example.voyageapp.databinding.ActivityFriendsBinding
import com.example.voyageapp.models.ModelUser
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_add_friends.*

class FriendsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFriendsBinding

    private lateinit var navigationViewTop: BottomNavigationView

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var mUser: ModelUser

    private lateinit var adapterUser: AdapterUser

    private lateinit var userList: ArrayList<ModelUser>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        //init firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()
        userList = ArrayList()
        mUser = ModelUser()
        //setup adapter
        adapterUser = AdapterUser(this@FriendsActivity, userList)
        //set adapter to recyclerview
        binding.friendsRv.adapter = adapterUser
        loadUsers()

        navigationViewTop = findViewById(R.id.linearTopIdFriends)
        navigationViewTop.selectedItemId = R.id.nav_friends
        // Perform item seleceted listener

        navigationViewTop.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_friends -> return@OnNavigationItemSelectedListener true

                R.id.nav_chats -> {
                    startActivity(Intent(applicationContext, ChatActivity::class.java))
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

    }

    private fun loadUsers() {

        val mRef = FirebaseDatabase.getInstance().getReference("Friends")
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.addValueEventListener(object : ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                //(mUsers as ArrayList<ModelUser>).clear()
                userList.clear()
                for (ds in snapshot.children) {
                    val model = ds.getValue(ModelUser::class.java)

                    if (model?.uid == firebaseAuth.uid){
                        if (model != null) {
                            mUser = model
                        }
                    }

                    mRef.child(firebaseAuth.uid!!).child("friends")
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
                                    mRef.removeEventListener(this)
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
                adapterUser.isClickable = false
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
    }

    override fun onStart() {
        super.onStart()
        online("online")
    }

    override fun onPause() {
        super.onPause()
        online("offline")
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        //startActivity(Intent(this, ChatActivity::class.java))
        super.onBackPressed()
        navigationViewTop.selectedItemId = R.id.nav_chats
        overridePendingTransition(0,0)
        finish()
    }
}