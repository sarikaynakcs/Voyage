package com.example.voyageapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.voyageapp.R
import com.example.voyageapp.databinding.ActivityAddFriendsBinding
import com.example.voyageapp.models.ModelUser
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_add_friends.*
import kotlinx.android.synthetic.main.add_friends_item.view.*

class AddFriendsActivity : AppCompatActivity(), FriendsAdapter.Listener {

    private lateinit var binding: ActivityAddFriendsBinding

    private lateinit var navigationViewTop: BottomNavigationView

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var mUsers: List<ModelUser>

    private lateinit var mUser: ModelUser

    private lateinit var mAdapter: FriendsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFriendsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        //init firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()
        mAdapter = FriendsAdapter(this)

        navigationViewTop = findViewById(R.id.linearTopIdAddFriends)
        navigationViewTop.selectedItemId = R.id.nav_add
        // Perform item seleceted listener

        navigationViewTop.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_add -> return@OnNavigationItemSelectedListener true

                R.id.nav_chats -> {
                    startActivity(Intent(applicationContext, ChatActivity::class.java))
                    finish()
                    overridePendingTransition(0,0)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_friends -> {
                    startActivity(Intent(applicationContext, FriendsActivity::class.java))
                    finish()
                    overridePendingTransition(0,0)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })

        binding.sendBtn.setOnClickListener {
            validateData()
        }

    }

    private var username = ""
    private var barcode = ""
    private fun validateData() {
        username = binding.usernameEt.text.toString().trim()
        barcode = binding.barcodeIdEt.text.toString().trim()

        if (username.isEmpty()){
            Toast.makeText(this, "Enter a username...", Toast.LENGTH_SHORT).show()
        }
        else if (barcode.isEmpty()){
            Toast.makeText(this, "Enter a barcode ID..", Toast.LENGTH_SHORT).show()
        }
        else{
            loadUser()
        }
    }

    private fun loadUser(){
        mUsers = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.addValueEventListener(object : ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                //clear list before starting adding data into it
                (mUsers as ArrayList<ModelUser>).clear()
                for (ds in snapshot.children){
                    val model = ds.getValue(ModelUser::class.java)

                    if (model?.uid == firebaseAuth.uid){
                        if (model != null) {
                            mUser = model
                        }
                    }

                    if (model?.barcodeId == barcode && model.username == username){
                        if (mUser.barcodeId == model.barcodeId && mUser.username == model.username){
                            Toast.makeText(this@AddFriendsActivity, "You cannot add yourself as a friend!", Toast.LENGTH_SHORT).show()
                        }else{
                            (mUsers as ArrayList<ModelUser>).add(model)
                        }
                    }
                }
                binding.friendsRv.adapter = mAdapter
                friendsRv.layoutManager = LinearLayoutManager(this@AddFriendsActivity)
                mAdapter.update(mUsers as ArrayList<ModelUser>, mUser.friends)
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




class FriendsAdapter(private val listener: Listener)
    : RecyclerView.Adapter<FriendsAdapter.ViewHolder> () {
    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    interface Listener {
        fun follow(uid: String)
        fun  unfollow(uid: String)
    }

    private var mUsers: List<ModelUser> = listOf<ModelUser>()
    private var mFollows: Map<String, Boolean> = mapOf<String, Boolean>()
    private var mPositions: Map<String, Int> = mapOf<String, Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.add_friends_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            val user = mUsers[position]
            view.username_text.text = user.username
            view.name_text.text = user.name
            Glide.with(view)
                .load(mUsers[position].profileImage)
                .placeholder(R.drawable.ic_person_gray)
                .into(view.photo_image)
            view.follow_btn.setOnClickListener { listener.follow(user.uid) }
            view.unfollow_btn.setOnClickListener { listener.unfollow(user.uid) }

            val follows = mFollows[user.uid] ?: false
            if (follows) {
                view.follow_btn.visibility = View.GONE
                view.unfollow_btn.visibility = View.VISIBLE
            }else{
                view.follow_btn.visibility = View.VISIBLE
                view.unfollow_btn.visibility = View.GONE
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update(users: java.util.ArrayList<ModelUser>, follows: Map<String, Boolean>) {
        mUsers = users
        mPositions = mUsers.withIndex().map { (idx, user) -> user.uid to idx }.toMap()
        mFollows = follows
        notifyDataSetChanged()
    }
    fun followed(uid: String){
        mFollows += (uid to true)
        notifyItemChanged(mPositions[uid]!!)
    }

    fun unfollowed(uid: String) {
        mFollows -= uid
        notifyItemChanged(mPositions[uid]!!)
    }
    override fun getItemCount() = mUsers.size
}