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
import com.example.voyageapp.databinding.ActivityGamePrizeBinding
import com.example.voyageapp.models.ModelUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_game_prize.*

class GamePrizeActivity : AppCompatActivity(), FriendsAdapter.Listener {

    private lateinit var binding: ActivityGamePrizeBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var mUsers: List<ModelUser>

    private lateinit var mUser: ModelUser

    private lateinit var mAdapter: FriendsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGamePrizeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        firebaseAuth = FirebaseAuth.getInstance()
        mAdapter = FriendsAdapter(this)
        loadUser()

        binding.goNextPgBtn.setOnClickListener {
            val intent = Intent(this@GamePrizeActivity, GameActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(0,0)
        }
    }

    private fun loadUser() {
        mUsers = ArrayList()
        mUser = ModelUser()
        val museum = intent.getStringExtra("museum")

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //clear list before starting adding data into it
                (mUsers as ArrayList<ModelUser>).clear()
                for (ds in snapshot.children) {
                    val model = ds.getValue(ModelUser::class.java)

                    if (model?.uid == firebaseAuth.uid){
                        if (model != null) {
                            mUser = model
                        }
                    }
                    ref.child(model?.uid!!).child("games").addValueEventListener(object : ValueEventListener{
                        @SuppressLint("NotifyDataSetChanged")
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (sd in snapshot.children) {
                                val uid = sd.key

                                if (uid == museum) {
                                    (mUsers as ArrayList<ModelUser>).add(model)
                                    mAdapter.notifyDataSetChanged()
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })

                }
                //ref.removeEventListener(this)
                binding.prizeRv.adapter = mAdapter
                prizeRv.layoutManager = LinearLayoutManager(this@GamePrizeActivity)
                mAdapter.update(mUsers as ArrayList<ModelUser>, mUser.friends)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

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

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        startActivity(Intent(this@GamePrizeActivity, GameActivity::class.java))
        overridePendingTransition(0,0)
        finish()
    }
}