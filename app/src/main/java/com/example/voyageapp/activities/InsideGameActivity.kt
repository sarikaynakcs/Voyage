package com.example.voyageapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.voyageapp.Common.Common
import com.example.voyageapp.R
import com.example.voyageapp.Remote.IGoogleAPIService
import com.example.voyageapp.databinding.ActivityInsideGameBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class InsideGameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInsideGameBinding

    private var random=(1..3).random()
    companion object{
        private const val MY_PERMISSION_CODE: Int = 2000 //1000
    }
    lateinit var mServices: IGoogleAPIService
//----------------------------------------------------
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInsideGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        mServices = Common.googleAPIService

        var idc = 0
        //-----------------------------------------
        val long =intent.getStringExtra("museum")
        binding.museumName.text=long

        readData("$long")
        val number= random.toString()


        binding.goToAnswer.setOnClickListener{
            val intent= Intent(this@InsideGameActivity, QuizQuestionsActivity::class.java)
            intent.putExtra("museum", long)
            intent.putExtra("number",number)
            startActivity(intent)
            overridePendingTransition(0,0)

            true
        }
        binding.clueOne.setOnClickListener {
            val intent= Intent(this@InsideGameActivity, ZoomClueActivity::class.java)
            intent.putExtra("museum", long)
            intent.putExtra("number",number)
            idc=1
            val idcstring=idc.toString()
            intent.putExtra("idc",idcstring)
            startActivity(intent)
            overridePendingTransition(0,0)

            true

        }
        binding.clueTwo.setOnClickListener {
            val intent= Intent(this@InsideGameActivity, ZoomClueActivity::class.java)
            intent.putExtra("museum", long)
            intent.putExtra("number",number)
            idc=2
            val idcstring=idc.toString()
            intent.putExtra("idc",idcstring)
            startActivity(intent)
            overridePendingTransition(0,0)

            true

        }
        binding.clueThree.setOnClickListener {
            val intent= Intent(this@InsideGameActivity, ZoomClueActivity::class.java)
            intent.putExtra("museum", long)
            intent.putExtra("number",number)
            idc=3
            val idcstring=idc.toString()
            intent.putExtra("idc",idcstring)
            startActivity(intent)
            overridePendingTransition(0,0)

            true

        }

    }

    private fun readData(name: String){

        database = FirebaseDatabase.getInstance().getReference("GameUpdate")
        database.child(name).child("examples").child("example$random").get().addOnSuccessListener {
            if(it.exists()){
                val museumName=it.child("name").value
                val clueOne=it.child("clue1").value
                val clueTwo=it.child("clue2").value
                val clueThree=it.child("clue3").value

                Toast.makeText(this,"Successfully",Toast.LENGTH_SHORT).show()
                binding.clueOne.text=clueOne.toString()
                binding.clueTwo.text=clueTwo.toString()
                binding.clueThree.text=clueThree.toString()
                binding.museumName.text=museumName.toString()

            }
            else{
                Toast.makeText(this,"Museum Does not exist",Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener{
            Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0,0)
    }
}
