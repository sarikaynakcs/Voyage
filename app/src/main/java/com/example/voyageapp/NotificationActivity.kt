package com.example.voyageapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.voyageapp.databinding.ActivityNotificationBinding
import com.google.firebase.auth.FirebaseAuth

class NotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationBinding

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        /*handle click, notification button
        binding.notificationButton.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }*/

        //handle click, information button
        binding.informationButton.setOnClickListener {
            startActivity(Intent(this@NotificationActivity, DashboardUserActivity::class.java))
        }

        //handle click, game button
        binding.gameButton.setOnClickListener {
            startActivity(Intent(this@NotificationActivity, GameActivity::class.java))
        }

        //handle click, chat button
        binding.chatButton.setOnClickListener {
            startActivity(Intent(this@NotificationActivity, ChatActivity::class.java))
        }

        //handle click, profile button
        binding.profileButton.setOnClickListener {
            startActivity(Intent(this@NotificationActivity, ProfileActivity::class.java))
        }
    }
}