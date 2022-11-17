package com.example.voyageapp.activities

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.voyageapp.R
import com.example.voyageapp.databinding.ActivityRegisterBinding
import com.example.voyageapp.models.ModelMuseum
import com.example.voyageapp.models.ModelUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding:ActivityRegisterBinding

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    //progress dialog
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        //init progress dialog, will show while creating account | register user
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        //handle back button, click goto previous screen
        binding.backBtn.setOnClickListener {
            onBackPressed() //goto previous screen
        }

        //handle click, begin register
        binding.registerBtn.setOnClickListener {
            /*Steps
            * 1) Input data
            * 2) Validate data
            * 3) Create account - Firebase Auth
            * 4) Save user info - Firebase realtime Database*/
            validateData()
        }

    }

    private var name = ""
    private var username = ""
    private var email = ""
    private var password = ""

    private fun validateData() {
        //1) Input data
        name = binding.nameEt.text.toString().trim()
        username = binding.usermameEt.text.toString().trim()
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()
        val cPassword = binding.cPasswordEt.text.toString().trim()

        //2) Validate data
        if (name.isEmpty()) {
            Toast.makeText(this, "Enter your name...", Toast.LENGTH_SHORT).show()
        }
        else if (username.isEmpty()) {
            Toast.makeText(this, "Enter your username...", Toast.LENGTH_SHORT).show()
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //invalid email pattern
            Toast.makeText(this, "Invalid Email Pattern...", Toast.LENGTH_SHORT).show()
        }
        else if (password.isEmpty()){
            Toast.makeText(this, "Enter password...", Toast.LENGTH_SHORT).show()
        }
        else if (cPassword.isEmpty()){
            Toast.makeText(this, "Confirm password...", Toast.LENGTH_SHORT).show()
        }
        else if (password != cPassword){
            Toast.makeText(this, "Password doesn't match...", Toast.LENGTH_SHORT).show()
        }
        else{
            createUserAccount()
        }
    }

    private fun createUserAccount() {
        //3) Create account - Firebase auth

        //show progress
        progressDialog.setMessage("Creating Account...")
        progressDialog.show()

        //create user in firebase auth
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            //account created, now add user info in db
            updateUserInfo()
        }
        .addOnFailureListener { e->
            //failed creating account
            progressDialog.dismiss()
            Toast.makeText(this, "Failed creating account due to ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUserInfo() {
        //4) Save user info - Firebase realtime Database

        progressDialog.setMessage("Saving user info...")

        //timestamp
        val timestamp = System.currentTimeMillis()

        var barcodeId = generateOTP()
        val mRef = FirebaseDatabase.getInstance().getReference("Users")
        mRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds in snapshot.children){
                    val model = ds.getValue(ModelUser::class.java)
                    val userName = ds.child("username").getValue(String::class.java)

                    if (userName == username && barcodeId == model?.barcodeId) {
                        barcodeId = generateOTP()
                        while (barcodeId != model.barcodeId){
                            barcodeId = generateOTP()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        //get current user uid, since user is registered so we can get it now
        val uid = firebaseAuth.uid

        //setup data to add in db
        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["uid"] = uid
        hashMap["email"] = email
        hashMap["name"] = name
        hashMap["username"] = username
        hashMap["profileImage"] = "" //empty now, do in profile edit
        hashMap["userType"] = "user" //possible values are user/admin, will change value to admin manually on firebase db
        hashMap["timestamp"] = timestamp
        hashMap["friends"] = ""
        hashMap["barcodeId"] = barcodeId

        //set data to db
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                //user info saved, open user dashboard
                progressDialog.dismiss()
                Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@RegisterActivity, DashboardUserActivity::class.java))
                finish()
            }
            .addOnFailureListener { e->
                //failed adding data to db
                progressDialog.dismiss()
                Toast.makeText(this, "Failed saving user info due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
        nameEt.text.clear()
        usermameEt.text.clear()
        emailEt.text.clear()
        passwordEt.text.clear()
        cPasswordEt.text.clear()
    }

    private fun generateOTP(): String {
        val randomPin = (Math.random() * 9000).toInt() + 1000
        return randomPin.toString()
    }
}