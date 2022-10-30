package com.example.voyageapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.voyageapp.databinding.ActivityGameBinding
import com.google.firebase.auth.FirebaseAuth

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        //handle click, notification button
        binding.notificationButton.setOnClickListener {
            startActivity(Intent(this@GameActivity, NotificationActivity::class.java))
        }

        //handle click, information button
        binding.informationButton.setOnClickListener {
            startActivity(Intent(this@GameActivity, DashboardUserActivity::class.java))
        }

        /*handle click, game button
        binding.gameButton.setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
        }*/

        //handle click, chat button
        binding.chatButton.setOnClickListener {
            startActivity(Intent(this@GameActivity, ChatActivity::class.java))
        }

        //handle click, profile button
        binding.profileButton.setOnClickListener {
            startActivity(Intent(this@GameActivity, ProfileActivity::class.java))
        }
    }
}