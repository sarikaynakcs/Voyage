package com.example.voyageapp.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.voyageapp.R
import com.example.voyageapp.databinding.ActivityChangeMuseumImageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.example.voyageapp.adapters.AdapterMuseum
import com.example.voyageapp.models.ModelMuseum
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.museum_view.*

class ChangeMuseumImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangeMuseumImageBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private var imageUri: Uri?=null

    private lateinit var adapterMuseum: AdapterMuseum

    private lateinit var museumArrayList: ArrayList<ModelMuseum>

    private lateinit var progressDialog: ProgressDialog

    private val TAG = "IMAGE_ADD_TAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeMuseumImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        //configure progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.backBtn.setOnClickListener{
            onBackPressed()
        }
        binding.museumImageId.setOnClickListener{
            showImageAttachMenu()
        }
        binding.updateButton.setOnClickListener {
            validateData()
        }

    }

    private fun validateData() {
        if (imageUri == null){
            Toast.makeText(this, "Pick an Image...", Toast.LENGTH_LONG).show()
        }
        else{
            getIncomingIntent()
        }
    }

    private fun uploadImage(id: String) {
        Log.d(TAG, "uploadImage: Uploading to storage...")
        //show progress dialog
        progressDialog.setMessage("Uploading Image...")
        progressDialog.show()

        //timestamp
        val timestamp = System.currentTimeMillis()

        //path of image in firebase storage
        val filePathAndName = "MuseumImages/$timestamp"
        //storage reference
        val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)
        storageReference.putFile(imageUri!!)
            .addOnSuccessListener {taskSnapshot->
                Log.d(TAG, "uploadImage: Image uploaded now getting url...")
                //Get url of uploaded image
                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val uploadedImageUrl = "${uriTask.result }"

                uploadImageToDb(uploadedImageUrl, id)
            }
            .addOnFailureListener { e->
                Log.d(TAG, "uploadImage: Failed to upload due to ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to upload due to ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun uploadImageToDb(uploadedImageUrl: String, id: String) {
        Log.d(TAG, "uploadImageToDb: Uploading to db")
        progressDialog.setMessage("Uploading image info...")

        //setup data to upload
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["museumImage"] = uploadedImageUrl

        val ref = FirebaseDatabase.getInstance().getReference("Museums")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds in snapshot.children){
                    val museumName = ds.child("museumName").getValue(String::class.java)
                    val mid = "${ds.child("id").value}"
                    var museumImage = ds.child("museumImage").getValue(String::class.java)

                    if (mid == id){
                        ref.child(id)
                            .updateChildren(hashMap)
                                progressDialog.dismiss()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun getIncomingIntent() {
        if (intent.hasExtra("id")){
            val id = intent.getStringExtra("id")
            loadMuseumInfo(id)
            if (id != null) {
                uploadImage(id)
            }
        }
    }

    private fun loadMuseumInfo(id: String?) {
        Log.d(TAG, "loadMuseumInfo: Loading museum informations")

        val ref = FirebaseDatabase.getInstance().getReference("Museums")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds in snapshot.children){
                    val museumName = ds.child("museumName").getValue(String::class.java)
                    val mid = "${ds.child("id").value}"
                    val museumImage = "${ds.child("museumImage").value}"

                    if (mid == id){
                        try {
                            Glide.with(this@ChangeMuseumImageActivity)
                                .load(museumImage)
                                .placeholder(R.drawable.ic_person_gray)
                                .into(binding.museumImageId)
                        }
                        catch (e: Exception){

                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


    private fun showImageAttachMenu() {
        val popupMenu = PopupMenu(this, binding.museumImageId)
        popupMenu.menu.add(Menu.NONE,0,0,"Gallery")
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { item ->
            val id = item.itemId
            if (id == 0){
                pickImageGallery()
            }

            true
        }
    }

    private fun pickImageGallery() {
        val intent= Intent(Intent.ACTION_PICK)
        intent.type="image/*"
        galleryActivityResultLauncher.launch(intent)
    }

    //used to handle of gallery intent (new wat in replacement of startactivityforresults)
    private  val galleryActivityResultLauncher= registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { result ->
            if(result.resultCode == Activity.RESULT_OK){
                Toast.makeText(this,"Image Picked", Toast.LENGTH_LONG).show()
                val data = result.data
                imageUri = data!!.data

                //set to imageview
                binding.museumImageId.setImageURI(imageUri)
            }
            else{
                //cancelled
                Toast.makeText(this,"Cancelled", Toast.LENGTH_LONG).show()
            }

        }
    )



}