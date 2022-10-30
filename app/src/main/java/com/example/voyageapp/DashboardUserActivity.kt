package com.example.voyageapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.voyageapp.databinding.ActivityDashboardUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class DashboardUserActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityDashboardUserBinding

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        //handle click, logout
        binding.logoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        //handle click, notification button
        binding.notificationButton.setOnClickListener {
            startActivity(Intent(this@DashboardUserActivity, NotificationActivity::class.java))
        }

        /*handle click, information button
        binding.informationButton.setOnClickListener {
            startActivity(Intent(this, DashboardUserActivity::class.java))
        }*/

        //handle click, game button
        binding.gameButton.setOnClickListener {
            startActivity(Intent(this@DashboardUserActivity, GameActivity::class.java))
        }

        //handle click, chat button
        binding.chatButton.setOnClickListener {
            startActivity(Intent(this@DashboardUserActivity, ChatActivity::class.java))
        }

        //handle click, profile button
        binding.profileButton.setOnClickListener {
            startActivity(Intent(this@DashboardUserActivity, ProfileActivity::class.java))
        }
    }

    private fun checkUser() {
        //get current user
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null){
            //not logged in, user can stay in user dashboard without login too
            binding.subTitleTv.text = "Not logged In"
        }
        else{
            //logged in, get and show user info
            val email = firebaseUser.email
            //set to textview of toolbar
            binding.subTitleTv.text = email
        }
    }
}