package com.example.voyageapp.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.voyageapp.activities.InsideChatActivity
import com.example.voyageapp.R
import com.example.voyageapp.activities.ShowProfileActivity
import com.example.voyageapp.models.ModelMessage
import com.example.voyageapp.models.ModelUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdapterUser(val context: Context, val userList: ArrayList<ModelUser>):
    RecyclerView.Adapter<AdapterUser.UserViewHolder>() {

    public var isClickable: Boolean = true
    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.chat_view, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, @SuppressLint("RecyclerView") position: Int) {
        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        val currentUser = userList[position]

        holder.name.text = currentUser.name

        Glide.with(context)
            .load(userList[position].profileImage)
            .placeholder(R.drawable.ic_person_gray)
            .into(holder.photo)

        val senderRoom = currentUser.uid + firebaseAuth.currentUser?.uid
        val mRef = FirebaseDatabase.getInstance().getReference("Chats").child(senderRoom).child("Messages")

        mRef.addChildEventListener(object : ChildEventListener{
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    mRef.orderByKey().limitToLast(1)
                        .addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (ds in snapshot.children) {
                                    val message = ds.child("message").value
                                    holder.message.text = message.toString()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                        })
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        mRef.removeEventListener(this)
                    }
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        val dbRef = FirebaseDatabase.getInstance().getReference("Blocklist")
        dbRef.child(currentUser.uid).child("blocklist")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChild(firebaseAuth.uid!!)) {
                        holder.name.text = "Voyage Kullanıcısı"
                        holder.message.visibility = View.GONE
                        holder.photo.setImageResource(R.drawable.ic_person_white)
                    } else {

                        dbRef.child(firebaseAuth.uid!!).child("blocklist")
                            .addValueEventListener(object : ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.hasChild(currentUser.uid)) {
                                        holder.name.text = "Voyage Kullanıcısı"
                                        holder.message.visibility = View.GONE
                                        holder.photo.setImageResource(R.drawable.ic_person_white)
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


        holder.itemView.setOnClickListener {
            val intent = Intent(context, InsideChatActivity::class.java)
            val activity = context as Activity
            intent.putExtra("name", currentUser.name)
            intent.putExtra("uid", currentUser.uid)
            context.startActivity(intent)
            //context.finish()
            activity.overridePendingTransition(0,0)
        }

        holder.photo.setOnClickListener {
            val intent = Intent(context, ShowProfileActivity::class.java)
            val activity = context as Activity
            intent.putExtra("uid", currentUser.uid)
            context.startActivity(intent)
            //context.finish()
            activity.overridePendingTransition(0,0)
        }

        val sender = currentUser.uid + firebaseAuth.currentUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("Chats")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild(sender)) {
                    holder.itemView.setOnLongClickListener {

                        if (isClickable) {
                            if (!holder.delete.isVisible) {
                                Toast.makeText(context, "Çöp kutusuna tıklayarak sohbeti silebilirsiniz.", Toast.LENGTH_SHORT).show()
                                holder.delete.visibility = View.VISIBLE
                            }
                            else {
                                holder.delete.visibility = View.GONE
                            }
                        }

                        true
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        holder.delete.setOnClickListener {
            if (isClickable) {
                //confirm before delete
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Sohbeti Sil")
                    .setMessage("Bu sohbeti silmek istediğinizden emin misiniz?")
                    .setPositiveButton("Evet"){a, d->
                        Toast.makeText(context, "Siliniyor...", Toast.LENGTH_SHORT).show()
                        deleteChat(currentUser.uid, holder)
                        userList.remove(userList[position])
                    }
                    .setNegativeButton("Hayır"){a, d->
                        a.dismiss()
                    }
                    .show()
            }
        }
    }

    private fun deleteChat(uid: String, holder: UserViewHolder) {
        val senderRoom = uid + firebaseAuth.currentUser?.uid
        val databaseRef = FirebaseDatabase.getInstance().getReference("isChat")

        FirebaseDatabase.getInstance().getReference("Chats").child(senderRoom).removeValue()
        databaseRef.child(firebaseAuth.uid!!).child("chats").child(uid).removeValue()
        Toast.makeText(context, "Sohbet silindi...", Toast.LENGTH_SHORT).show()
        holder.delete.visibility = View.GONE

    }

    override fun getItemCount(): Int {
        return  userList.size
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val name: TextView = itemView.findViewById(R.id.name_txt)
        val photo: de.hdodenhof.circleimageview.CircleImageView = itemView.findViewById(R.id.photo_img)
        val message: TextView = itemView.findViewById(R.id.message_txt)
        val delete: ImageButton = itemView.findViewById(R.id.deleteChatBtn)
    }
}

