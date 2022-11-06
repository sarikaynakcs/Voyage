package com.example.voyageapp.activities

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.voyageapp.R
import com.example.voyageapp.adapters.AdapterMuseum
import com.example.voyageapp.databinding.ActivityMuseumDetailBinding
import com.example.voyageapp.models.ModelCategory
import com.example.voyageapp.models.ModelMuseum
import com.google.firebase.auth.FirebaseAuth

class MuseumDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMuseumDetailBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog: ProgressDialog

    private lateinit var museumArrayList: ArrayList<ModelMuseum>

    private lateinit var adapterMuseum: AdapterMuseum

    private lateinit var categoryArrayList: ArrayList<ModelCategory>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMuseumDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        //configure progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        getIncomingIntent()
    }

    private fun getIncomingIntent() {
        if (intent.hasExtra("museumName") && intent.hasExtra("museumType") && intent.hasExtra("overview") && intent.hasExtra("history")
                && intent.hasExtra("exhibitions") && intent.hasExtra("establishment") && intent.hasExtra("museumCity")){

            val museumName = intent.getStringExtra("museumName")
            binding.title.text = museumName

            val museumType = intent.getStringExtra("museumType")
            binding.subtitle.text = museumType

            val overview = intent.getStringExtra("overview")
            binding.overviewInfo.text = overview

            val history = intent.getStringExtra("history")
            binding.historyInfo.text = history

            val exhibitions = intent.getStringExtra("exhibitions")
            binding.exhibitionsInfo.text = exhibitions

            val establishment = intent.getStringExtra("establishment")
            binding.establishment.text = establishment

            val museumCity = intent.getStringExtra("museumCity")
            binding.museumCity.text = museumCity

            if (museumCity == "İstanbul"){
                binding.topIv.setImageResource(R.drawable.back03)
            }
        }
    }
}