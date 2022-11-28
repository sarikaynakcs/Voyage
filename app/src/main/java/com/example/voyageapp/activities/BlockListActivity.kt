package com.example.voyageapp.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voyageapp.R
import com.example.voyageapp.adapters.AdapterBlock
import com.example.voyageapp.databinding.ActivityBlockListBinding
import com.example.voyageapp.models.ModelUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BlockListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBlockListBinding

    private lateinit var adapterBlock: AdapterBlock

    private lateinit var userList: ArrayList<ModelUser>

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlockListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        //init firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()
        //init arraylist
        userList = ArrayList()
        //setup adapter
        adapterBlock = AdapterBlock(this@BlockListActivity, userList)
        //set adapter to recyclerview
        binding.blocksRv.layoutManager = LinearLayoutManager(this)
        binding.blocksRv.adapter = adapterBlock
        loadUsers()
    }

    private fun loadUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        val mRef = FirebaseDatabase.getInstance().getReference("Blocklist")

        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (ds in snapshot.children) {
                    val model = ds.getValue(ModelUser::class.java)

                    mRef.child(firebaseAuth.uid!!).child("blocklist")
                        .addValueEventListener(object : ValueEventListener{
                            @SuppressLint("NotifyDataSetChanged")
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.hasChild(model!!.uid)) {
                                    userList.add(model)
                                    adapterBlock.notifyDataSetChanged()
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

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0,0)
    }
}