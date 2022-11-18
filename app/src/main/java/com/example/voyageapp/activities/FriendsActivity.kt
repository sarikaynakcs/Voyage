package com.example.voyageapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voyageapp.R
import com.example.voyageapp.adapters.AdapterUser
import com.example.voyageapp.databinding.ActivityFriendsBinding
import com.example.voyageapp.models.ModelUser
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_add_friends.*

class FriendsActivity : AppCompatActivity(), FriendsAdapter.Listener {

    private lateinit var binding: ActivityFriendsBinding

    private lateinit var navigationViewTop: BottomNavigationView

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var mUsers: List<ModelUser>

    private lateinit var mUser: ModelUser

    private lateinit var mAdapter: FriendsAdapter

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
        mAdapter = FriendsAdapter(this)
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
        //init arraylist
        //mUsers = ArrayList()
        userList = ArrayList()
        mUser = ModelUser()

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
                    ref.child(firebaseAuth.uid!!).child("friends").addValueEventListener(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (sd in snapshot.children){
                                val uid = sd.key

                                if (uid == model?.uid) {
                                    if (model != null) {
                                        //(mUsers as ArrayList<ModelUser>).add(model)
                                        userList.add(model)
                                    }
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }

                    })
                    /*if (mUser.friends.keys.contains(model?.uid)){
                        if (model != null) {
                            (mUsers as ArrayList<ModelUser>).add(model)
                        }
                    }*/
                }
                //binding.friendsRv.adapter = mAdapter
                //friendsRv.layoutManager = LinearLayoutManager(this@FriendsActivity)
                //mAdapter.update(mUsers as ArrayList<ModelUser>, mUser.friends)

                //setup adapter
                adapterUser = AdapterUser(this@FriendsActivity, userList)
                adapterUser.isClickable = false
                //set adapter to recyclerview
                binding.friendsRv.adapter = adapterUser
                adapterUser.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        startActivity(Intent(this, ChatActivity::class.java))
        overridePendingTransition(0,0)
        finish()
    }

    override fun follow(uid: String) {
        setFollow(uid, true){
            mAdapter.followed(uid)
        }
    }

    override fun unfollow(uid: String) {
        setFollow(uid, false){
            mAdapter.unfollowed(uid)
        }
    }

    private fun setFollow(uid: String, follow: Boolean, onSucces: () -> Unit){
        val ref = FirebaseDatabase.getInstance().getReference("Users")

        val followTask = ref.child(uid).child("friends").child(firebaseAuth.uid!!)
        val setFollow = if (follow) followTask.setValue(true) else followTask.removeValue()

        val followersTask = ref.child(firebaseAuth.uid!!).child("friends").child(uid)
        val setFollower = if (follow) followersTask.setValue(true) else followersTask.removeValue()

        setFollow.continueWithTask({setFollower}).addOnCompleteListener {
            if (it.isSuccessful) {
                onSucces()
            }else{
                Toast.makeText(this, "Failed due to ${it.exception!!.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}