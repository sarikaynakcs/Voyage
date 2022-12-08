package com.example.voyageapp.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voyageapp.R
import com.example.voyageapp.adapters.AdapterGameScore
import com.example.voyageapp.databinding.ActivityInsideLeaderBoardBinding
import com.example.voyageapp.models.ModelUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class InsideLeaderBoardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInsideLeaderBoardBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var adapterGamesScore: AdapterGameScore

    private lateinit var  userList: ArrayList<ModelUser>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInsideLeaderBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        firebaseAuth = FirebaseAuth.getInstance()
        val museumName = intent.getStringExtra("museum")
        loadUsers(museumName)

        if (museumName == "genel") {
            binding.title.text = "Genel Skor Tablosu"
            binding.textTv.text = "Oyuncuların oynamış olduğu tüm oyunlardan elde ettikleri toplam puanı aşağıdaki listede görebilirsin!"
        } else {
            binding.title.text = museumName
            binding.textTv.text = "Bu oyunda puanın diğer oyunculardan düşük diye üzülme. Daha çok oyun oynayarak rakiplerinden daha fazla puan toplayabilir ve bunu Genel Skor Tablosu sayfasında görüntüleyebilirsin!"
        }
    }

    private fun loadUsers(museum: String?) {
        userList = ArrayList()

        val mRef = FirebaseDatabase.getInstance().getReference("PlayerGames")
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        if (museum == "genel") {
            adapterGamesScore = AdapterGameScore(this@InsideLeaderBoardActivity, userList,2, museum)
            binding.gameInsideLeaderRv.layoutManager = LinearLayoutManager(this@InsideLeaderBoardActivity)
            binding.gameInsideLeaderRv.adapter = adapterGamesScore

            ref.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    userList.clear()
                    for (ds in snapshot.children) {
                        val model = ds.getValue(ModelUser::class.java)

                        mRef.addListenerForSingleValueEvent(object : ValueEventListener{
                            @SuppressLint("NotifyDataSetChanged")
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (sd in snapshot.children) {
                                    val users = sd.key

                                    if (users == model!!.uid) {
                                        userList.add(model)
                                        adapterGamesScore.notifyDataSetChanged()
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
        } else {
            adapterGamesScore = AdapterGameScore(this@InsideLeaderBoardActivity, userList,1, museum!!)
            binding.gameInsideLeaderRv.layoutManager = LinearLayoutManager(this@InsideLeaderBoardActivity)
            binding.gameInsideLeaderRv.adapter = adapterGamesScore

            ref.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    userList.clear()
                    for (ds in snapshot.children) {
                        val model = ds.getValue(ModelUser::class.java)

                        mRef.addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.hasChild(model!!.uid)) {
                                    mRef.child(model.uid).child("games")
                                        .addValueEventListener(object : ValueEventListener{
                                            @SuppressLint("NotifyDataSetChanged")
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                for (sd in snapshot.children) {
                                                    val museumName = sd.key

                                                    if (museumName == museum) {
                                                        userList.add(model)
                                                        adapterGamesScore.notifyDataSetChanged()
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

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(0,0)
    }
}