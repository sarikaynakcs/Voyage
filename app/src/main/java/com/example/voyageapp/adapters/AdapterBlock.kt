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
import com.google.firebase.database.FirebaseDatabase

class AdapterBlock(val context: Context, val userList: ArrayList<ModelUser>):
    RecyclerView.Adapter<AdapterBlock.BlockViewHolder>() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.blocklist_item, parent, false)
        return BlockViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: BlockViewHolder, position: Int) {
        firebaseAuth = FirebaseAuth.getInstance()

        val currentUser = userList[position]

        holder.name.text = currentUser.name
        holder.username.text = currentUser.username

        Glide.with(context)
            .load(userList[position].profileImage)
            .placeholder(R.drawable.ic_person_gray)
            .into(holder.photo)

        holder.unblock.setOnClickListener {
            val ref = FirebaseDatabase.getInstance().getReference("Blocklist")
            ref.child(firebaseAuth.uid!!).child("blocklist").child(currentUser.uid).removeValue()
            userList.remove(userList[position])
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class BlockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.name_block)
        val username: TextView = itemView.findViewById(R.id.username_block)
        val photo: de.hdodenhof.circleimageview.CircleImageView = itemView.findViewById(R.id.photo_image_block)
        val unblock: TextView = itemView.findViewById(R.id.unblock_btn_block)
    }

}