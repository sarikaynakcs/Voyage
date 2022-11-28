package com.example.voyageapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.voyageapp.R
import com.google.firebase.auth.FirebaseAuth

class AdapterGame(val context: Context, val gameList: ArrayList<String>):
    RecyclerView.Adapter<AdapterGame.GameViewHolder>() {

    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        firebaseAuth = FirebaseAuth.getInstance()

        val currentMuseum = gameList[position]
        holder.name.text = currentMuseum
    }

    override fun getItemCount(): Int {
        return gameList.size
    }

    class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.museumNameProfile)
    }
}