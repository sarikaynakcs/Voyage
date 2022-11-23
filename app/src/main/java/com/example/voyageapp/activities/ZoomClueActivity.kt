package com.example.voyageapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.voyageapp.R
import com.example.voyageapp.databinding.ActivityZoomClueBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ZoomClueActivity : AppCompatActivity() {
    private lateinit var binding: ActivityZoomClueBinding

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityZoomClueBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        val long =intent.getStringExtra("museum")
        readData("$long")

    }
    private fun readData(name: String){
        val number =intent.getStringExtra("number")
        val idc =intent.getStringExtra("idc")

        database = FirebaseDatabase.getInstance().getReference("GameUpdate")
        database.child(name).child("examples").child("example$number").get().addOnSuccessListener {
            if(it.exists()){
                val museumName=it.child("name").value
                val clueOne=it.child("clue1").value
                val clueTwo=it.child("clue2").value
                val clueThree=it.child("clue3").value

                Toast.makeText(this,"Successfully", Toast.LENGTH_SHORT).show()

                if(idc=="1"){
                    binding.ipuclar.text=clueOne.toString()
                    binding.museumName.text=museumName.toString()
                }
                if(idc=="2"){
                    binding.ipuclar.text=clueTwo.toString()
                    binding.museumName.text=museumName.toString()
                }
                if(idc=="3"){
                    binding.ipuclar.text=clueThree.toString()
                    binding.museumName.text=museumName.toString()
                }

            }
            else{
                Toast.makeText(this,"Museum Does not exist", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener{
            Toast.makeText(this,"Failed", Toast.LENGTH_SHORT).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0,0)
    }
}