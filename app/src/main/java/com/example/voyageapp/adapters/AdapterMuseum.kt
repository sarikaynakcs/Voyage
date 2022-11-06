package com.example.voyageapp.adapters

import android.content.Context
import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.*
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.voyageapp.activities.MuseumDetailActivity
import com.example.voyageapp.databinding.MuseumViewBinding
import com.example.voyageapp.filters.FilterMuseum
import com.example.voyageapp.models.ModelMuseum
import com.example.voyageapp.R
import com.google.firebase.database.FirebaseDatabase

class AdapterMuseum :RecyclerView.Adapter<AdapterMuseum.HolderMuseum>, Filterable {

    private val context: Context
    public var museumArrayList: ArrayList<ModelMuseum>
    private var filterList: ArrayList<ModelMuseum>

    private var filter: FilterMuseum? = null

    private lateinit var binding: MuseumViewBinding

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

        //set data
        holder.museumName.text = museumName
        holder.museumType.text = museumType
        holder.museumCity.text = museumCity
        holder.establishment.text = establishment

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
            intent.putExtra("museumName", museumName)
            intent.putExtra("museumType", museumType)
            intent.putExtra("overview", overview)
            intent.putExtra("history", history)
            intent.putExtra("exhibitions", exhibitions)
            intent.putExtra("establishment", establishment)
            intent.putExtra("museumCity", museumCity)
            context.startActivity(intent)
        }

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
        var museumImg:ImageView = binding.museumImg
    }

    override fun getFilter(): Filter {
        if (filter == null){
            filter = FilterMuseum(filterList, this)
        }
        return filter as FilterMuseum
    }
}