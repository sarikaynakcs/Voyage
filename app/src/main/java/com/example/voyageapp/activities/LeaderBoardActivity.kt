package com.example.voyageapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voyageapp.R
import com.example.voyageapp.adapters.AdapterGame
import com.example.voyageapp.adapters.AdapterGamesMuseum
import com.example.voyageapp.databinding.ActivityLeaderBoardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LeaderBoardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLeaderBoardBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var adapterGamesMuseum: AdapterGamesMuseum

    private lateinit var gameList: ArrayList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaderBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        gameList = ArrayList()
        adapterGamesMuseum = AdapterGamesMuseum(this@LeaderBoardActivity, gameList)
        binding.gameLeaderRv.layoutManager = LinearLayoutManager(this)
        binding.gameLeaderRv.adapter = adapterGamesMuseum

        firebaseAuth = FirebaseAuth.getInstance()
        loadMuseumName()

        binding.goLdrBoardBtn.setOnClickListener {
            val intent = Intent(this@LeaderBoardActivity, InsideLeaderBoardActivity::class.java)
            intent.putExtra("museum", "genel")
            startActivity(intent)
            overridePendingTransition(0,0)
        }
    }

    private fun loadMuseumName() {
        val ref = FirebaseDatabase.getInstance().getReference("GameUpdate")

        ref.addValueEventListener(object : ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds in snapshot.children) {
                    val gameName = ds.key

                    if (!gameList.contains(gameName)) {
                        gameList.add(gameName!!)
                        adapterGamesMuseum.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(0,0)
    }
}