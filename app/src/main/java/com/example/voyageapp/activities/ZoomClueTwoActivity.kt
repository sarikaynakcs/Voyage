package com.example.voyageapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.voyageapp.databinding.ActivityZoomClueTwoBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ZoomClueTwoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityZoomClueTwoBinding

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityZoomClueTwoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val long =intent.getStringExtra("museum")
        readData("$long")

    }

    private fun readData(name: String){
        val number =intent.getStringExtra("number")

        database = FirebaseDatabase.getInstance().getReference("GameUpdate")
        database.child(name).child("examples").child("example$number").get().addOnSuccessListener {
            if(it.exists()){
                val museumName=it.child("name").value
                val clueTwo=it.child("clue2").value
                Toast.makeText(this,"Successfully", Toast.LENGTH_SHORT).show()
                binding.clueTwo.text=clueTwo.toString()
                binding.museumName.text=museumName.toString()

            }
            else{
                Toast.makeText(this,"Museum Does not exist", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener{
            Toast.makeText(this,"Failed", Toast.LENGTH_SHORT).show()
        }
    }
}