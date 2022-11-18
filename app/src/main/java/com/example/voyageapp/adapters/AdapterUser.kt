package com.example.voyageapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.voyageapp.activities.InsideChatActivity
import com.example.voyageapp.R
import com.example.voyageapp.models.ModelUser

class AdapterUser(val context: Context, val userList: ArrayList<ModelUser>):
    RecyclerView.Adapter<AdapterUser.UserViewHolder>() {

    public var isClickable: Boolean = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.chat_view, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

        val currentUser = userList[position]
        holder.name.text = currentUser.name

        Glide.with(context)
            .load(userList[position].profileImage)
            .placeholder(R.drawable.ic_person_gray)
            .into(holder.photo)

        holder.itemView.setOnClickListener {
            if (isClickable == false){
                return@setOnClickListener
            }else {
                val intent = Intent(context, InsideChatActivity::class.java)
                intent.putExtra("name", currentUser.name)
                intent.putExtra("uid", currentUser.uid)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return  userList.size
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val name: TextView = itemView.findViewById(R.id.name_txt)
        val photo: de.hdodenhof.circleimageview.CircleImageView = itemView.findViewById(R.id.photo_img)
        val message: TextView = itemView.findViewById(R.id.message_txt)
    }
}

