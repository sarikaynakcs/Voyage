package com.example.voyageapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
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
    private lateinit var timer: CountDownTimer

    private var random=(1..3).random()
    companion object{
        private const val MY_PERMISSION_CODE: Int = 2000 //1000
    }
    lateinit var mServices: IGoogleAPIService
//----------------------------------------------------
    private lateinit var database: DatabaseReference
    var checkAnswer: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInsideGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        mServices = Common.googleAPIService

        //-----------------------------------------
        val long =intent.getStringExtra("museum")


        timer = object : CountDownTimer(10000,1000) {
            override fun onFinish() {
                binding.sayac.text = "Left: 0"
                checkAnswer = false
                val intent=Intent(this@InsideGameActivity,GameScoreActivity::class.java)
                intent.putExtra("check",checkAnswer.toString())
                startActivity(intent)
                finish()
                overridePendingTransition(0,0)
            }

            override fun onTick(millisUntilFinished: Long) {
                binding.sayac.text = "Left: ${millisUntilFinished/1000}"
            }

        }.start()

        readData("$long")
        val number= random.toString()


    }

    private fun readData(name: String){

        database = FirebaseDatabase.getInstance().getReference("GameUpdate")
        database.child(name).child("examples").child("example$random").get().addOnSuccessListener {
            if(it.exists()){
                val museumName=it.child("name").value
                val answer=it.child("answer").value
                val clueOne=it.child("clue1").value
                val clueTwo=it.child("clue2").value
                val clueThree=it.child("clue3").value
                val optionOne=it.child("option1").value
                val optionTwo=it.child("option2").value
                val optionThree=it.child("option3").value
                val optionFour=it.child("option4").value

                Toast.makeText(this,"Successfully",Toast.LENGTH_SHORT).show()
                binding.clueOne.text=clueOne.toString()
                binding.clueTwo.text=clueTwo.toString()
                binding.clueThree.text=clueThree.toString()
                binding.option1.text=optionOne.toString()
                binding.option2.text=optionTwo.toString()
                binding.option3.text=optionThree.toString()
                binding.option4.text=optionFour.toString()
                var id= 0

                binding.option1.setOnClickListener{
                    binding.submit.visibility = View.VISIBLE
                    binding.option1.setBackgroundColor(ContextCompat.getColor(this, R.color.gray01))
                    binding.option2.setBackgroundColor(ContextCompat.getColor(this,
                        R.color.secondary
                    ))
                    binding.option3.setBackgroundColor(ContextCompat.getColor(this,
                        R.color.secondary
                    ))
                    binding.option4.setBackgroundColor(ContextCompat.getColor(this,
                        R.color.secondary
                    ))
                    id= 1

                }
                binding.option2.setOnClickListener{
                    binding.submit.visibility = View.VISIBLE
                    binding.option2.setBackgroundColor(ContextCompat.getColor(this, R.color.gray01))
                    binding.option1.setBackgroundColor(ContextCompat.getColor(this,
                        R.color.secondary
                    ))
                    binding.option3.setBackgroundColor(ContextCompat.getColor(this,
                        R.color.secondary
                    ))
                    binding.option4.setBackgroundColor(ContextCompat.getColor(this,
                        R.color.secondary
                    ))
                    id= 2

                }
                binding.option3.setOnClickListener{
                    binding.submit.visibility = View.VISIBLE
                    binding.option3.setBackgroundColor(ContextCompat.getColor(this, R.color.gray01))
                    binding.option2.setBackgroundColor(ContextCompat.getColor(this,
                        R.color.secondary
                    ))
                    binding.option1.setBackgroundColor(ContextCompat.getColor(this,
                        R.color.secondary
                    ))
                    binding.option4.setBackgroundColor(ContextCompat.getColor(this,
                        R.color.secondary
                    ))
                    id= 3

                }
                binding.option4.setOnClickListener{
                    binding.submit.visibility = View.VISIBLE
                    binding.option4.setBackgroundColor(ContextCompat.getColor(this, R.color.gray01))
                    binding.option3.setBackgroundColor(ContextCompat.getColor(this,
                        R.color.secondary
                    ))
                    binding.option2.setBackgroundColor(ContextCompat.getColor(this,
                        R.color.secondary
                    ))
                    binding.option1.setBackgroundColor(ContextCompat.getColor(this,
                        R.color.secondary
                    ))
                    id= 4

                }


                binding.submit.setOnClickListener {

                    if(id == 1){
                        if(binding.option1.text==answer){
                            binding.option1.setBackgroundColor(ContextCompat.getColor(this,
                                R.color.green
                            ))
                            checkAnswer = true
                        }
                        else{
                            binding.option1.setBackgroundColor(ContextCompat.getColor(this,
                                R.color.red
                            ))
                            checkAnswer = false
                        }
                    }
                    else if(id == 2){
                        if(binding.option2.text==answer){
                            binding.option2.setBackgroundColor(ContextCompat.getColor(this,
                                R.color.green
                            ))
                            checkAnswer = true
                        }
                        else{
                            binding.option2.setBackgroundColor(ContextCompat.getColor(this,
                                R.color.red
                            ))
                            checkAnswer = false
                        }
                    }
                    else if(id == 3){
                        if(binding.option3.text==answer){
                            binding.option3.setBackgroundColor(ContextCompat.getColor(this,
                                R.color.green
                            ))
                            checkAnswer = true
                        }
                        else{
                            binding.option3.setBackgroundColor(ContextCompat.getColor(this,
                                R.color.red
                            ))
                            checkAnswer = false
                        }
                    }
                    else if(id == 4){
                        if(binding.option4.text==answer){
                            binding.option4.setBackgroundColor(ContextCompat.getColor(this,
                                R.color.green
                            ))
                            checkAnswer = true
                        }
                        else{
                            binding.option4.setBackgroundColor(ContextCompat.getColor(this,
                                R.color.red
                            ))
                            checkAnswer = false
                        }
                    }
                    binding.option1.isEnabled=false
                    binding.option1.isClickable=false

                    binding.option2.isEnabled=false
                    binding.option2.isClickable=false

                    binding.option3.isEnabled=false
                    binding.option3.isClickable=false

                    binding.option4.isEnabled=false
                    binding.option4.isClickable=false

                    binding.submit.visibility= View.GONE
                    binding.submit.isEnabled=false
                    binding.next.visibility= View.VISIBLE
                    timer.cancel()

                    binding.next.setOnClickListener {
                        val intent= Intent(this@InsideGameActivity, GameScoreActivity::class.java)
                        intent.putExtra("check", checkAnswer.toString())
                        intent.putExtra("museum", name)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                        overridePendingTransition(0,0)
                    }
                }

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
        if (binding.submit.isEnabled){
            super.onBackPressed()
            timer.cancel()
            overridePendingTransition(0,0)
            finish()
        }
    }
}
