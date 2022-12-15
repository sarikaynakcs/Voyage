package com.example.voyageapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.core.content.ContextCompat
import com.example.voyageapp.R
import com.example.voyageapp.databinding.ActivityInsideThirdGameBinding
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_inside_third_game.*

class InsideThirdGameActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var binding: ActivityInsideThirdGameBinding
    private lateinit var answerList: ArrayList<String>
    private lateinit var timer: CountDownTimer
    var id=0
    override fun onCreate(savedInstanceState: Bundle?) {
        binding=ActivityInsideThirdGameBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        var score = intent.getStringExtra("score")
        binding.time.text=score.toString()

        timer = object : CountDownTimer(100000,1000) {
            override fun onFinish() {
                binding.sayac.text = "Kalan Zaman: 0"
                //checkAnswer = false
                val intent=Intent(this@InsideThirdGameActivity,GameScoreActivity::class.java)
                //intent.putExtra("check",checkAnswer.toString())
                intent.putExtra("score", (score!!.toInt()).toString())
                startActivity(intent)
                finish()
                overridePendingTransition(0,0)
            }

            override fun onTick(millisUntilFinished: Long) {

                binding.sayac.text = "Kalan Zaman: ${millisUntilFinished/1000}"
            }

        }.start()

        val names = ArrayList<String>()
        var one=""
        var two=""
        var three=""
        var four=""

        answerList= ArrayList()
        val long = intent.getStringExtra("museum").toString()

        database = FirebaseDatabase.getInstance().getReference("GameUpdate")
        database.child(long).child("examples").child("ThirdGameExample").get().addOnSuccessListener {
            if(it.exists()){
                val text1 =it.child("question1").value
                val text2 =it.child("question2").value
                val text3 =it.child("question3").value
                val text4 =it.child("question4").value

                binding.example1.text=text1.toString()
                binding.example2.text=text2.toString()
                binding.example3.text=text3.toString()
                binding.example4.text=text4.toString()
            }
        }

        database = FirebaseDatabase.getInstance().getReference("GameUpdate")
        database.child(long).child("examples").child("ThirdGameExample").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                names.add("")
                for (ds in snapshot.children){
                    val spinnerName="${ds.child("name").value}"
                    if (spinnerName!="null"){
                        names.add(spinnerName)
                    }
                }
                val arrayAdapter= ArrayAdapter(this@InsideThirdGameActivity,R.layout.spinner_item,names)
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
                spinner1.adapter = arrayAdapter
                spinner2.adapter = arrayAdapter
                spinner3.adapter = arrayAdapter
                spinner4.adapter = arrayAdapter
                spinner1.onItemSelectedListener=object : AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        one=names[position]
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }
                }
                spinner2.onItemSelectedListener=object : AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        two=names[position]
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }
                }
                spinner3.onItemSelectedListener=object : AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        three=names[position]
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }
                }
                spinner4.onItemSelectedListener=object : AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        four=names[position]
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }
                }
                binding.button.setOnClickListener{
                    id=1
                    timer.cancel()
                    database = FirebaseDatabase.getInstance().getReference("GameUpdate")
                    database.child("Anadolu Medeniyetleri Müzesi").child("examples").child("ThirdGameExample").get()
                        .addOnSuccessListener {
                            if (it.exists()){
                                val answer1 = it.child("answer1").value
                                val answer2 = it.child("answer2").value
                                val answer3 = it.child("answer3").value
                                val answer4 = it.child("answer4").value

                                val intent=Intent(this@InsideThirdGameActivity, GameScoreActivity::class.java)
                                if(one==answer1.toString() && two==answer2.toString() && three==answer3.toString() && four==answer4.toString()){
                                    intent.putExtra("score", (score!!.toInt()+5).toString())
                                    intent.putExtra("museum", long)
                                }else{
                                    intent.putExtra("score", (score!!.toInt()).toString())
                                    intent.putExtra("museum", long)
                                }
                                startActivity(intent)
                                finish()
                                overridePendingTransition(0,0)
                            }
                        }


                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

    }
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (id==1){
            super.onBackPressed()
            overridePendingTransition(0,0)
            finish()
        }
    }
}