package com.example.voyageapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.voyageapp.databinding.ActivityDashboardUserBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class DashboardUserActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityDashboardUserBinding

    private lateinit var navigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        navigationView = findViewById(R.id.linearBotId)

        supportFragmentManager.beginTransaction()
            .replace(R.id.body_container, InformationFragment()).commit()
        navigationView.setSelectedItemId(R.id.nav_information)
        navigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            var fragment: Fragment? = null
            when (item.itemId) {
                R.id.nav_information -> fragment = InformationFragment()
                R.id.nav_notification -> fragment = NotificationFragment()
                R.id.nav_game -> fragment = GameFragment()
                R.id.nav_chat -> fragment = ChatFragment()
                R.id.nav_profile -> fragment = ProfileFragment()
            }
            supportFragmentManager.beginTransaction().replace(R.id.body_container, fragment!!)
                .commit()
            true
        })
    }
}
