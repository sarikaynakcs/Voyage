package com.example.voyageapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.voyageapp.R
import com.example.voyageapp.models.ModelUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView

class AdapterGameScore(val context: Context, val userList: ArrayList<ModelUser>, val scoreType: Int, val museumName: String):
    RecyclerView.Adapter<AdapterGameScore.GameScoreViewHolder>() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameScoreViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.player_score_item, parent, false)
        return GameScoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameScoreViewHolder, position: Int) {
        firebaseAuth = FirebaseAuth.getInstance()

        val currentUser = userList[position]

        holder.name.text = currentUser.name
        holder.username.text = currentUser.username

        Glide.with(context)
            .load(userList[position].profileImage)
            .placeholder(R.drawable.ic_person_gray)
            .into(holder.photo)

        if (scoreType == 1) {
            val mRef = FirebaseDatabase.getInstance().getReference("PlayerGames")

            mRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChild(currentUser.uid)) {
                        mRef.child(currentUser.uid).child("games")
                            .addValueEventListener(object : ValueEventListener {
                                @SuppressLint("NotifyDataSetChanged")
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    for (sd in snapshot.children) {
                                        val museum = sd.key
                                        val score = sd.value

                                        if (museum == museumName) {
                                            holder.score.text = score.toString()
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

        if (scoreType == 2) {
            val ref = FirebaseDatabase.getInstance().getReference("PlayerGames")
            var generalScore = 0

            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChild(currentUser.uid)) {
                        ref.child(currentUser.uid).child("games")
                            .addValueEventListener(object : ValueEventListener {
                                @SuppressLint("NotifyDataSetChanged")
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    for (sd in snapshot.children) {
                                        val museum = sd.key
                                        val score = sd.value
                                        generalScore += score.toString().toInt()
                                    }
                                    holder.score.text = generalScore.toString()
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

    override fun getItemCount(): Int {
        return userList.size
    }

    class GameScoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.name_txt_game)
        val username: TextView = itemView.findViewById(R.id.username_txt_game)
        val photo: CircleImageView = itemView.findViewById(R.id.photo_img_game)
        val score: TextView = itemView.findViewById(R.id.score_txt_game)
    }
}