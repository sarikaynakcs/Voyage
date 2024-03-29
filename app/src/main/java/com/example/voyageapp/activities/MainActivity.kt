package com.example.voyageapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.voyageapp.R
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
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            overridePendingTransition(0,0)
        }

        //handle click, skip and continue to main screen
        binding.skipBtn.setOnClickListener() {
            startActivity(Intent(this, RegisterActivity::class.java))
            overridePendingTransition(0,0)
        }
    }
}