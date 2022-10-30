package com.example.voyageapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.voyageapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        //handle click, login
        binding.loginBtn.setOnClickListener() {
            //will do later
            startActivity(Intent(this, LoginActivity::class.java))
        }

        //hand click, skip and continue to main screen
        binding.skipBtn.setOnClickListener() {
            //will do later
            startActivity(Intent(this, DashboardUserActivity::class.java))
        }


    }
}