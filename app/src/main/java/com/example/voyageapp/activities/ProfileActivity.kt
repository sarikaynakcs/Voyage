package com.example.voyageapp.activities

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.voyageapp.R
import com.example.voyageapp.adapters.AdapterGame
import com.example.voyageapp.databinding.ActivityProfileBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle

    private lateinit var navigationView: BottomNavigationView

    private lateinit var binding: ActivityProfileBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var adapterGame: AdapterGame

    private lateinit var gameList: ArrayList<String>

    //progress dialog
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        gameList = ArrayList()
        adapterGame = AdapterGame(this@ProfileActivity, gameList)
        binding.recyclerViewProfile.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        binding.recyclerViewProfile.adapter = adapterGame

        //init firebaseauth
        firebaseAuth = FirebaseAuth.getInstance()
        loadUserInfo()
        loadMuseumName()

        binding.apply {
            toggle= ActionBarDrawerToggle(this@ProfileActivity,drawerLayout,R.string.open,R.string.close)
            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()

            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            navView.itemIconTintList=null

            navView.setNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.firstItem->{
                        val intent=Intent(this@ProfileActivity,ProfileUpdateActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(0,0)
                    }
                    R.id.secondItem->{
                        val intent=Intent(this@ProfileActivity,BlockListActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(0,0)
                    }
                    R.id.thirdItem->{
                        firebaseAuth.signOut()
                        val intent = Intent(this@ProfileActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        overridePendingTransition(0,0)
                    }
                }
                true
            }
        }

        //init progress dialog, will show while login user
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        navigationView = findViewById(R.id.linearBotIdProfile)

        navigationView.selectedItemId = R.id.nav_profile

        // Perform item selected listener
        navigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_profile -> return@OnNavigationItemSelectedListener true

                R.id.nav_information -> {
                    startActivity(Intent(applicationContext, DashboardUserActivity::class.java))
                    finish()
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_chat -> {
                    startActivity(Intent(applicationContext, ChatActivity::class.java))
                    finish()
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_game -> {
                    startActivity(Intent(applicationContext, GameActivity::class.java))
                    finish()
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })


    }

    private fun loadMuseumName() {

        val ref = FirebaseDatabase.getInstance().getReference("PlayerGames")

        ref.child(firebaseAuth.uid!!).child("games")
            .addChildEventListener(object : ChildEventListener{
                @SuppressLint("NotifyDataSetChanged")
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val gameName = snapshot.key
                    if (!gameList.contains(gameName)) {
                        gameList.add(gameName!!)
                        adapterGame.notifyDataSetChanged()
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    TODO("Not yet implemented")
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun loadUserInfo() {
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!)
            .addValueEventListener(object: ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {
                    val email = "${snapshot.child("email").value}"
                    val name = "${snapshot.child("name").value}"
                    val barcode = "${snapshot.child("barcodeId").value}"
                    val profileImage = "${snapshot.child("profileImage").value}"

                    fullNameId.text = name
                    emailId.text = email
                    barcodeId.text = barcode
                    findViewById<TextView>(R.id.nameHeader).text = name
                    findViewById<TextView>(R.id.emailHeader).text = email

                    Glide.with(this@ProfileActivity)
                        .load(profileImage)
                        .placeholder(R.drawable.ic_person_gray)
                        .into(findViewById(R.id.profileImageHeader))

                    try{
                        Glide.with(this@ProfileActivity)
                            .load(profileImage)
                            .placeholder(R.drawable.ic_person_gray)
                            .into(profileImageId)
                    }
                    catch (e: Exception){

                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        startActivity(Intent(this@ProfileActivity, DashboardUserActivity::class.java))
        overridePendingTransition(0,0)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            true
        }
        return super.onOptionsItemSelected(item)
    }

}