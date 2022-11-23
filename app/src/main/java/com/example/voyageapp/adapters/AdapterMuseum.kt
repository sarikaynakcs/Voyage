package com.example.voyageapp.adapters


import android.app.Activity
import android.content.Context
import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.*
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.voyageapp.activities.MuseumDetailActivity
import com.example.voyageapp.databinding.MuseumViewBinding
import com.example.voyageapp.filters.FilterMuseum
import com.example.voyageapp.models.ModelMuseum
import com.example.voyageapp.R
import com.example.voyageapp.activities.ChangeMuseumImageActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.System.load

class AdapterMuseum :RecyclerView.Adapter<AdapterMuseum.HolderMuseum>, Filterable {

    private val context: Context
    public var museumArrayList: ArrayList<ModelMuseum>
    private var filterList: ArrayList<ModelMuseum>

    private var filter: FilterMuseum? = null

    private lateinit var binding: MuseumViewBinding

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    //constructor
    constructor(context: Context, museumArrayList: ArrayList<ModelMuseum>) {
        this.context = context
        this.museumArrayList = museumArrayList
        this.filterList = museumArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderMuseum {
        //inflate bind museum_view.xml
        binding = MuseumViewBinding.inflate(LayoutInflater.from(context), parent,false)

        return HolderMuseum(binding.root)
    }

    override fun onBindViewHolder(holder: HolderMuseum, position: Int) {
        /*---- Get data, Set data, Handle clicks etc ----*/

        //get data
        val model = museumArrayList[position]
        val id = model.id
        val museumName = model.museumName
        val museumType = model.museumType
        val overview = model.overview
        val history = model.history
        val exhibitions = model.exhibitions
        val museumCity = model.museumCity
        val establishment = model.establishment
        val uid = model.uid
        val timestamp = model.timestamp
        val museumImg = model.museumImage

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser(holder)

        //set data
        holder.museumName.text = museumName
        holder.museumType.text = museumType
        holder.museumCity.text = museumCity
        holder.establishment.text = establishment

        Glide.with(context)
            .load(museumArrayList[position].museumImage)
            .into(holder.museumImg)

        //handle click, delete museum
        holder.deleteBtn.setOnClickListener {
            //confirm before delete
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Delete")
                .setMessage("Are you sure want to delete this museum?")
                .setPositiveButton("Confirm"){a, d->
                    Toast.makeText(context, "Deleting...", Toast.LENGTH_SHORT).show()
                    deleteMuseum(model, holder)
                }
                .setNegativeButton("Cancel"){a, d->
                    a.dismiss()
                }
                .show()
        }

        holder.itemView.findViewById<LinearLayout>(R.id.parent_layout_museum).setOnClickListener {
            val intent = Intent(context, MuseumDetailActivity::class.java)
            val activity = context as Activity
            intent.putExtra("museumName", museumName)
            intent.putExtra("museumType", museumType)
            intent.putExtra("overview", overview)
            intent.putExtra("history", history)
            intent.putExtra("exhibitions", exhibitions)
            intent.putExtra("establishment", establishment)
            intent.putExtra("museumCity", museumCity)
            context.startActivity(intent)
            activity.overridePendingTransition(0,0)
        }

        holder.uploadBtn.setOnClickListener {
            val intent = Intent(context, ChangeMuseumImageActivity::class.java)
            val activity = context as Activity
            intent.putExtra("id", id)
            context.startActivity(intent)
            activity.overridePendingTransition(0,0)
        }

    }

    private fun checkUser(holder: HolderMuseum) {
        val firebaseUser = firebaseAuth.currentUser!!

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    //get user type e.g. user or admin
                    val userType = snapshot.child("userType").value
                    if (userType == "admin"){
                        holder.deleteBtn.visibility = View.VISIBLE
                        holder.uploadBtn.visibility = View.VISIBLE
                    }
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private fun deleteMuseum(model: ModelMuseum, holder: HolderMuseum) {
        //get id of museum to delete
        val id = model.id
        //Firebase DB > Museums > museumID
        val ref = FirebaseDatabase.getInstance().getReference("Museums")
        ref.child(id)
            .removeValue()
            .addOnSuccessListener {
                Toast.makeText(context, "Deleted...", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e->
                Toast.makeText(context, "Unable to delete due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    override fun getItemCount(): Int {
        return museumArrayList.size //number of items in list
    }

    inner class HolderMuseum(itemView: View): RecyclerView.ViewHolder(itemView){
        //init ui views
        var museumName:TextView = binding.title
        var museumType:TextView = binding.subtitle
        var museumCity:TextView = binding.museumCity
        var establishment:TextView = binding.establishment
        var deleteBtn:ImageButton = binding.deleteBtn
        var uploadBtn:ImageButton = binding.uploadBtn
        var museumImg:de.hdodenhof.circleimageview.CircleImageView = binding.museumImg
    }

    override fun getFilter(): Filter {
        if (filter == null){
            filter = FilterMuseum(filterList, this)
        }
        return filter as FilterMuseum
    }
}