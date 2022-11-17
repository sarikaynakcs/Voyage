package com.example.voyageapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.example.voyageapp.R
import com.example.voyageapp.databinding.ActivityNotificationBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class NotificationActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityNotificationBinding

    private lateinit var navigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        navigationView = findViewById(R.id.linearBotIdNotification)

        navigationView.selectedItemId = R.id.nav_notification

        // Perform item selected listener
        navigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_notification -> return@OnNavigationItemSelectedListener true

                R.id.nav_information -> {
                    startActivity(Intent(applicationContext, DashboardUserActivity::class.java))
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
                R.id.nav_chat -> {
                    startActivity(Intent(applicationContext, ChatActivity::class.java))
                    finish()
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(applicationContext, ProfileActivity::class.java))
                    finish()
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        startActivity(Intent(this@NotificationActivity, DashboardUserActivity::class.java))
        overridePendingTransition(0,0)
        finish()
    }
}