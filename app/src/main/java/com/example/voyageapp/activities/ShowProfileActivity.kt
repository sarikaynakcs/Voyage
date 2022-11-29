package com.example.voyageapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.voyageapp.R
import com.example.voyageapp.databinding.ActivityShowProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_profile.*

class ShowProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShowProfileBinding

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        firebaseAuth = FirebaseAuth.getInstance()
        val uid = intent.getStringExtra("uid")
        loadData(uid)
        checkUser(uid)

        binding.followBtn.setOnClickListener {

            binding.followBtn.visibility = View.GONE
            binding.unfollowBtn.visibility = View.VISIBLE

            val ref = FirebaseDatabase.getInstance().getReference("Friends")
            ref.child(uid!!).child("friends").child(firebaseAuth.uid!!).setValue(true)
            ref.child(firebaseAuth.uid!!).child("friends").child(uid).setValue(true)
        }

        binding.unfollowBtn.setOnClickListener {

            binding.followBtn.visibility = View.VISIBLE
            binding.unfollowBtn.visibility = View.GONE

            val mRef = FirebaseDatabase.getInstance().getReference("Friends")
            mRef.child(uid!!).child("friends").child(firebaseAuth.uid!!).removeValue()
            mRef.child(firebaseAuth.uid!!).child("friends").child(uid).removeValue()
        }

        binding.blockBtn.setOnClickListener {

            binding.blockBtn.visibility = View.GONE
            binding.unblockBtn.visibility = View.VISIBLE

            val dRef = FirebaseDatabase.getInstance().getReference("Blocklist")
            dRef.child(firebaseAuth.uid!!).child("blocklist").child(uid!!).setValue(true)
            //dRef.child(uid).child("blocklist").child(firebaseAuth.uid!!).setValue(true)

            val mRef = FirebaseDatabase.getInstance().getReference("Friends")
            mRef.child(uid).child("friends").child(firebaseAuth.uid!!).removeValue()
            mRef.child(firebaseAuth.uid!!).child("friends").child(uid).removeValue()
            Toast.makeText(this, "Bu kişiyi engellediniz!", Toast.LENGTH_SHORT).show()
            loadData(uid)
        }

        binding.unblockBtn.setOnClickListener {

            binding.blockBtn.visibility = View.VISIBLE
            binding.unblockBtn.visibility = View.GONE
            binding.followBtn.visibility = View.VISIBLE
            binding.unfollowBtn.visibility = View.VISIBLE

            val dbRef = FirebaseDatabase.getInstance().getReference("Blocklist")
            dbRef.child(firebaseAuth.uid!!).child("blocklist").child(uid!!).removeValue()
            //dbRef.child(uid).child("blocklist").child(firebaseAuth.uid!!).removeValue()
            loadData(uid)
        }

    }

    private fun checkUser(uid: String?) {
        val mRef = FirebaseDatabase.getInstance().getReference("Friends")
        val dRef = FirebaseDatabase.getInstance().getReference("Blocklist")

        dRef.child(uid!!).child("blocklist")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChild(firebaseAuth.uid!!)) {
                        binding.followBtn.visibility = View.GONE
                        binding.unfollowBtn.visibility = View.GONE
                        binding.blockBtn.visibility = View.GONE
                        binding.unblockBtn.visibility = View.GONE
                    }else {

                        dRef.child(firebaseAuth.uid!!).child("blocklist")
                            .addValueEventListener(object : ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.hasChild(uid)) {
                                        binding.unblockBtn.visibility = View.VISIBLE
                                        binding.blockBtn.visibility = View.GONE
                                    }else {
                                        binding.blockBtn.visibility = View.VISIBLE
                                        binding.unblockBtn.visibility = View.GONE

                                        mRef.child(firebaseAuth.uid!!).child("friends")
                                            .addValueEventListener(object : ValueEventListener{
                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                    if (snapshot.hasChild(uid)) {
                                                        binding.unfollowBtn.visibility = View.VISIBLE
                                                        binding.followBtn.visibility = View.GONE
                                                    }else {
                                                        binding.followBtn.visibility = View.VISIBLE
                                                        binding.unfollowBtn.visibility = View.GONE
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

    private fun loadData(uid: String?) {
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        val mRef = FirebaseDatabase.getInstance().getReference("Blocklist")

        mRef.child(uid!!).child("blocklist")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChild(firebaseAuth.uid!!)) {
                        binding.fullNameId.text = "Voyage Kullanıcısı"
                        binding.emailId.visibility = View.GONE
                        binding.barcodeId.visibility = View.GONE
                        binding.profileImageId.setImageResource(R.drawable.ic_person_white)
                        binding.barcodeText.visibility = View.GONE
                        binding.followBtn.visibility = View.GONE
                        binding.unfollowBtn.visibility = View.GONE
                        binding.blockBtn.visibility = View.GONE
                        binding.unblockBtn.visibility = View.GONE
                    }else {

                        mRef.child(firebaseAuth.uid!!).child("blocklist")
                            .addValueEventListener(object : ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.hasChild(uid)) {
                                        binding.fullNameId.text = "Voyage Kullanıcısı"
                                        binding.emailId.visibility = View.GONE
                                        binding.barcodeId.visibility = View.GONE
                                        binding.profileImageId.setImageResource(R.drawable.ic_person_white)
                                        binding.barcodeText.visibility = View.GONE
                                        binding.followBtn.visibility = View.GONE
                                        binding.unfollowBtn.visibility = View.GONE
                                    }else {

                                        ref.child(uid).addValueEventListener(object : ValueEventListener{
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                val email = "${snapshot.child("email").value}"
                                                val name = "${snapshot.child("name").value}"
                                                val barcode = "${snapshot.child("barcodeId").value}"
                                                val profileImage = "${snapshot.child("profileImage").value}"

                                                binding.emailId.visibility = View.VISIBLE
                                                binding.barcodeId.visibility = View.VISIBLE
                                                binding.barcodeText.visibility = View.VISIBLE

                                                binding.fullNameId.text = name
                                                binding.emailId.text = email
                                                binding.barcodeId.text = barcode

                                                try{
                                                    Glide.with(this@ShowProfileActivity)
                                                        .load(profileImage)
                                                        .placeholder(R.drawable.ic_person_gray)
                                                        .into(binding.profileImageId)
                                                }
                                                catch (e: Exception){

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

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        startActivity(Intent(this, ChatActivity::class.java))
        overridePendingTransition(0,0)
        finish()
    }

}