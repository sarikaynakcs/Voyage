package com.example.voyageapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import com.example.voyageapp.R
import com.example.voyageapp.databinding.ActivityQuizQuestionsBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class QuizQuestionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizQuestionsBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizQuestionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        val long =intent.getStringExtra("museum")
        readData("$long")
    }

    private fun readData(name: String){
        val number =intent.getStringExtra("number")
        database = FirebaseDatabase.getInstance().getReference("GameUpdate")
        database.child(name).child("examples").child("example$number").get().addOnSuccessListener {
            if(it.exists()){
                val museumName = it.child("name").value
                val answer=it.child("answer").value
                val optionOne=it.child("option1").value
                val optionTwo=it.child("option2").value
                val optionThree=it.child("option3").value

                Toast.makeText(this,"Successfully", Toast.LENGTH_SHORT).show()
                binding.museumName.text=museumName.toString()
                binding.option1.text=optionOne.toString()
                binding.option2.text=optionTwo.toString()
                binding.option3.text=optionThree.toString()
                var id= 0
                var checkAnswer: Boolean = true

                binding.option1.setOnClickListener{
                    binding.submit.visibility = View.VISIBLE
                    binding.option1.setBackgroundColor(ContextCompat.getColor(this, R.color.gray01))
                    binding.option2.setBackgroundColor(ContextCompat.getColor(this,
                        R.color.secondary
                    ))
                    binding.option3.setBackgroundColor(ContextCompat.getColor(this,
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
                    id= 3

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
                    binding.option1.isEnabled=false
                    binding.option1.isClickable=false

                    binding.option2.isEnabled=false
                    binding.option2.isClickable=false

                    binding.option3.isEnabled=false
                    binding.option3.isClickable=false

                    binding.submit.visibility= View.GONE
                    binding.submit.isEnabled=false
                    binding.next.visibility= View.VISIBLE

                    binding.next.setOnClickListener {
                        val intent= Intent(this@QuizQuestionsActivity, GameScoreActivity::class.java)
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
                Toast.makeText(this,"Museum Does not exist", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener{
            Toast.makeText(this,"Failed", Toast.LENGTH_SHORT).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.submit.isEnabled){
            super.onBackPressed()
            overridePendingTransition(0,0)
            finish()
        }
    }
}