package com.example.voyageapp.activities

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
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
            if (museumCity == "Sivas"){
                binding.topIv.setImageResource(R.drawable.back06)
            }
            if (museumCity == "Hatay"){
                binding.topIv.setImageResource(R.drawable.back07)
            }
            if (museumCity == "Van"){
                binding.topIv.setImageResource(R.drawable.back08)
            }
            if (museumCity == "Trabzon"){
                binding.topIv.setImageResource(R.drawable.back09)
            }
            if (museumCity == "Çanakkale"){
                binding.topIv.setImageResource(R.drawable.back10)
            }
            if (museumCity == "Gaziantep"){
                binding.topIv.setImageResource(R.drawable.back11)
            }
            if (museumCity == "İzmir"){
                binding.topIv.setImageResource(R.drawable.back12)
            }
            if (binding.historyInfo.text.isEmpty()){
                binding.historyTv.visibility = View.GONE
                binding.historyView.visibility = View.GONE
                binding.historyInfo.visibility = View.GONE
            }
            if (binding.exhibitionsInfo.text.isEmpty()) {
                binding.exhibitionsTv.visibility = View.GONE
                binding.exhibitionsView.visibility = View.GONE
                binding.exhibitionsInfo.visibility = View.GONE
            }
            if (binding.overviewInfo.text.isEmpty()) {
                binding.overviewTv.visibility = View.GONE
                binding.overviewView.visibility = View.GONE
                binding.overviewInfo.visibility = View.GONE
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        finish()
        overridePendingTransition(0,0)
    }
}