package com.example.voyageapp.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.voyageapp.R
import com.example.voyageapp.databinding.ActivityProfileUpdateBinding
import com.example.voyageapp.models.ModelUser
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class ProfileUpdateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileUpdateBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private var imageUri: Uri?=null

    private lateinit var progressDialog: ProgressDialog

    private lateinit var userList: ModelUser

    override fun onCreate(savedInstanceState: Bundle?) {
        binding=ActivityProfileUpdateBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()
        loadUserInfo()

        binding.backBtn.setOnClickListener{
            onBackPressed()
        }
        binding.profileImageId.setOnClickListener{
            showImageAttachMenu()
        }
        binding.updateButton.setOnClickListener{
            validateData()
        }
    }
    private var name =""
    private var username =""
    private fun validateData() {
        name = binding.nameId.text.toString().trim()
        username = binding.usernameId.text.toString().trim()
        if (name.isEmpty()){
            Toast.makeText(this,"Enter name", Toast.LENGTH_SHORT).show()
        }
        else if (username.isEmpty()){
            Toast.makeText(this,"Enter username", Toast.LENGTH_SHORT).show()
        }
        else{
            if (imageUri== null){
                updateProfile("")
            }
            else{
                uploadImage()
            }
        }
    }

    private fun uploadImage() {
        progressDialog.setMessage("Uploading profile image")
        progressDialog.show()

        //image path and name,use uid to replace previous
        val filePathAndName ="ProfileImages/"+firebaseAuth.uid

        //storage reference
        val reference = FirebaseStorage.getInstance().getReference(filePathAndName)
        reference.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                //image uploaded, get url of uploaded image
                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                while(!uriTask.isSuccessful);
                val uploadedImageUrl = "${uriTask.result}"

                updateProfile(uploadedImageUrl)
            }
            .addOnFailureListener{ e ->
                progressDialog.dismiss()
                Toast.makeText(this,"Failed to upload image due to $e.m", Toast.LENGTH_LONG).show()

            }
    }

    private fun updateProfile(uploadedImageUrl: String) {
        progressDialog.setMessage("Updating profile...")
        //setup info to db
        val hashMap: HashMap<String, Any> = HashMap()

        hashMap["username"] = username
        hashMap["name"] = name

        if (imageUri !=null){
            hashMap["profileImage"] = uploadedImageUrl
        }
        //update to db
        val reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.child(firebaseAuth.uid!!)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this,"Profile updated", Toast.LENGTH_LONG).show()

            }
            .addOnFailureListener{ e ->
                progressDialog.dismiss()
                Toast.makeText(this,"Failed to update profile due to $e.m", Toast.LENGTH_LONG).show()

            }
    }

    private fun loadUserInfo() {
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!)
            .addValueEventListener(object: ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = "${snapshot.child("name").value}"
                    val username = "${snapshot.child("username").value}"
                    val profileImage = "${snapshot.child("profileImage").value}"

                    // val formattedDate = Myapplication.formatTimeStamp(timestamp.toLong())

                    binding.nameId.setText(name)
                    binding.usernameId.setText(username)

                    try{
                        Glide.with(this@ProfileUpdateActivity)
                            .load(profileImage)
                            .placeholder(R.drawable.ic_person_gray)
                            .into(binding.profileImageId)
                    }
                    catch (e: Exception){

                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
    private fun showImageAttachMenu(){
        val popupMenu = PopupMenu(this,binding.profileImageId)
        popupMenu.menu.add(Menu.NONE,0,0,"Camera")
        popupMenu.menu.add(Menu.NONE,1,1,"Gallery")
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { item ->

            val id=item.itemId
            if (id == 0){
                pickImageCamera()
            }
            else if (id==1){
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

    private fun pickImageCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE,"Temp_Title")
        values.put(MediaStore.Images.Media.DESCRIPTION,"Temp_Description")

        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri)
        cameraActivityResultLauncher.launch(intent)

    }
    private val cameraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { result ->
            //get uri of image
            if(result.resultCode == Activity.RESULT_OK){
                val data = result.data
                //imageUri = data!!.data no need we already have image in imageUri in camera case

                //set to imageview
                binding.profileImageId.setImageURI(imageUri)
            }
            else{
                //cancelled
                Toast.makeText(this,"Cancelled", Toast.LENGTH_LONG).show()
            }
        }
    )
    //used to handle of gallery intent (new wat in replacement of startactivityforresults)
    private  val galleryActivityResultLauncher= registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { result ->
            if(result.resultCode == Activity.RESULT_OK){
                val data = result.data
                imageUri = data!!.data
                //imageUri = data!!.data no need we already have image in imageUri in camera case

                //set to imageview
                binding.profileImageId.setImageURI(imageUri)
            }
            else{
                //cancelled
                Toast.makeText(this,"Cancelled", Toast.LENGTH_LONG).show()
            }

        }
    )

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0,0)
    }
}