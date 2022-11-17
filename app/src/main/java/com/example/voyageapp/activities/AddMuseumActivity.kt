package com.example.voyageapp.activities

import android.app.AlertDialog
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.constraintlayout.widget.Constraints.TAG
import androidx.core.content.ContextCompat
import com.example.voyageapp.R
import com.example.voyageapp.databinding.ActivityAddMuseumBinding
import com.example.voyageapp.models.ModelCategory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_add_museum.*

class AddMuseumActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMuseumBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog: ProgressDialog

    private lateinit var categoryArrayList: ArrayList<ModelCategory>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMuseumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        loadMuseumCategories()

        //configure progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        //handle click back button
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        //handle click show category pick dialog
        binding.museumCityTv.setOnClickListener {
            categoryPickDialog()
        }

        //handle click, begin upload category
        binding.submitBtn.setOnClickListener {
            validateData()
        }
    }

    private fun loadMuseumCategories() {
        Log.d(TAG, "loadMuseumCities: Loading museum categories")
        //init arraylist
        categoryArrayList = ArrayList()

        //db reference to load categories DB -> Categories
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //clear list before adding data
                categoryArrayList.clear()
                for (ds in snapshot.children){
                    //get data
                    val model = ds.getValue(ModelCategory::class.java)
                    //add to arraylist
                    categoryArrayList.add(model!!)
                    Log.d(TAG, "onDataChange: ${model.category}")
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private var selectedCategoryID = ""
    private var selectedCategoryTitle = ""

    private fun categoryPickDialog() {
        Log.d(TAG, "categoryPickDialog: Showing cities category pick dialog")

        //get string array categories from arraylist
        val  categoriesArray = arrayOfNulls<String>(categoryArrayList.size)
        for (i in categoryArrayList.indices){
            categoriesArray[i] = categoryArrayList[i].category
        }

        //alert dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick Category")
            .setItems(categoriesArray){dialog, which ->
                //handle item click
                //get clicked item
                selectedCategoryTitle = categoryArrayList[which].category
                selectedCategoryID = categoryArrayList[which].id
                //set category to textview
                binding.museumCityTv.text = selectedCategoryTitle

                Log.d(TAG, "categoryPickDialog: Selected Category ID: $selectedCategoryID")
                Log.d(TAG, "categoryPickDialog: Selected Category Title: $selectedCategoryTitle")
            }
            .show()
    }

    private var museumName = ""
    private var museumType = ""
    private var museumCity = ""
    private var establishment = ""

    private fun validateData() {
        //validate data

        //get data
        museumName = binding.museumNameEt.text.toString().trim()
        museumType = binding.museumTypeEt.text.toString().trim()
        museumCity = binding.museumCityTv.text.toString().trim()
        establishment = binding.establishmentEt.text.toString().trim()

        //validate data
        if (museumName.isEmpty()){
            Toast.makeText(this, "Enter a Museum name...", Toast.LENGTH_SHORT).show()
        }
        else if (museumType.isEmpty()){
            Toast.makeText(this, "Enter a Museum Type...", Toast.LENGTH_SHORT).show()
        }
        else if(museumCity.isEmpty()){
            Toast.makeText(this, "Enter the city of the Museum...", Toast.LENGTH_SHORT).show()
        }
        else if (establishment.isEmpty()){
            Toast.makeText(this, "Enter an Establishment year...", Toast.LENGTH_SHORT).show()
        }
        else{
            addMuseumFirebase()
        }
    }

    private fun addMuseumFirebase() {
        //show progress
        progressDialog.show()

        //get timestamp
        val timestamp = System.currentTimeMillis()

        //setup data to add in firebase db
        val hashMap = HashMap<String, Any>()
        hashMap["id"] = "$timestamp"
        hashMap["museumName"] = museumName
        hashMap["museumType"] = museumType
        hashMap["overview"] = ""
        hashMap["history"] = ""
        hashMap["exhibitions"] = ""
        hashMap["museumCity"] = museumCity
        hashMap["establishment"] = establishment
        hashMap["museumImage"] = "" //empty now, do later
        hashMap["timestamp"] = timestamp
        hashMap["uid"] = "${firebaseAuth.uid}"

        val ref = FirebaseDatabase.getInstance().getReference("Museums")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                //added successfully
                progressDialog.dismiss()
                Toast.makeText(this, "Added successfully...", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e->
                //failed to add
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to add due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
        museumNameEt.text.clear()
        museumTypeEt.text.clear()
        museumCityTv.text = ""
        establishmentEt.text.clear()
    }
}