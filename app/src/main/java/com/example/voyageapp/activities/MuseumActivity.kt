package com.example.voyageapp.activities

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.example.voyageapp.R
import com.example.voyageapp.adapters.AdapterMuseum
import com.example.voyageapp.databinding.ActivityMuseumBinding
import com.example.voyageapp.models.ModelMuseum
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.museum_view.*

class MuseumActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    private lateinit var myRef: DatabaseReference

    private lateinit var binding: ActivityMuseumBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog: ProgressDialog

    //arraylist to hold museums
    private lateinit var adapterMuseum: AdapterMuseum

    //adapter
    private lateinit var museumArrayList: ArrayList<ModelMuseum>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMuseumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        recyclerView = findViewById(R.id.museumsRv)
        getIncomingIntent()

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        //configure progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        //handle click, start add museum page
        binding.addMuseumBtn.setOnClickListener {
            startActivity(Intent(this@MuseumActivity, AddMuseumActivity::class.java))
            overridePendingTransition(0,0)
        }

        //handle search button
        binding.searchEt.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    adapterMuseum.filter.filter(s)
                }
                catch (e: Exception){

                }
            }
            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private fun checkUser() {
        val firebaseUser = firebaseAuth.currentUser!!

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    //get user type e.g. user or admin
                    val userType = snapshot.child("userType").value
                    if (userType == "admin"){
                        binding.addMuseumBtn.visibility = VISIBLE
                    }
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private fun loadMuseums(cityName: String?) {
        //init arraylist
        museumArrayList = ArrayList()

        //get all museums from firebase database... Firebase DB > Museums
        val ref = FirebaseDatabase.getInstance().getReference("Museums")
        ref.addValueEventListener(object : ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                //clear list before starting adding data into it
                museumArrayList.clear()
                for (ds in snapshot.children){
                    //get data as model
                    val model = ds.getValue(ModelMuseum::class.java)
                    val name = ds.child("museumCity").getValue(String::class.java)

                    if (cityName == name){
                        //add to arraylist
                        museumArrayList.add(model!!)
                    }
                }
                museumArrayList.sortBy {
                    it.museumName.lowercase()
                }
                //setup adapter
                adapterMuseum = AdapterMuseum(this@MuseumActivity, museumArrayList)
                //set adapter to recyclerview
                binding.museumsRv.adapter = adapterMuseum
                adapterMuseum.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun getIncomingIntent(){
        if (intent.hasExtra("name")){

            val cityName = intent.getStringExtra("name")
            loadMuseums(cityName)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        finish()
        overridePendingTransition(0,0)
    }
}