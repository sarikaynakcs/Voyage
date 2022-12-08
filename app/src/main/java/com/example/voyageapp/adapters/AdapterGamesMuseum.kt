package com.example.voyageapp.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.voyageapp.R
import com.example.voyageapp.activities.InsideLeaderBoardActivity
import com.google.firebase.auth.FirebaseAuth

class AdapterGamesMuseum(val context: Context, val gameList: ArrayList<String>):
    RecyclerView.Adapter<AdapterGamesMuseum.GamesMuseumViewHolder>() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GamesMuseumViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.museum_list_item, parent, false)
        return GamesMuseumViewHolder(view)
    }

    override fun onBindViewHolder(holder: GamesMuseumViewHolder, position: Int) {
        firebaseAuth = FirebaseAuth.getInstance()

        val currentMuseum = gameList[position]
        holder.name.text = currentMuseum

        holder.itemView.setOnClickListener {
            val intent = Intent(context, InsideLeaderBoardActivity::class.java)
            val activity = context as Activity
            intent.putExtra("museum",currentMuseum)
            context.startActivity(intent)
            activity.overridePendingTransition(0,0)
        }
    }

    override fun getItemCount(): Int {
        return gameList.size
    }

    class GamesMuseumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.museumGameTv)
    }
}