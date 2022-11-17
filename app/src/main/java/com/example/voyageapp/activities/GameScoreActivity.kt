package com.example.voyageapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.voyageapp.databinding.ActivityGameScoreBinding

class GameScoreActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameScoreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameScoreBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}