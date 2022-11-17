package com.example.voyageapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
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
                ""
                var id= 0


                binding.option1.setOnClickListener{
                    binding.option1.setBackgroundColor(ContextCompat.getColor(this, R.color.gray01))
                    binding.option2.setBackgroundColor(ContextCompat.getColor(this,
                        R.color.secondary
                    ))
                    binding.option3.setBackgroundColor(ContextCompat.getColor(this,
                        R.color.secondary
                    ))

                    id= 1
                    binding.option1


                    //if(binding.option1.text==answer)
                    //{
                    //    binding.option1.setBackgroundColor(ContextCompat.getColor(this,R.color.green))
                    //    binding.option2.setBackgroundColor(ContextCompat.getColor(this,R.color.red))
                    //    binding.option3.setBackgroundColor(ContextCompat.getColor(this,R.color.red))
                    //}
                    //else if (binding.option2.text==answer)
                    //{
                    //    binding.option2.setBackgroundColor(ContextCompat.getColor(this,R.color.green))
                    //    binding.option1.setBackgroundColor(ContextCompat.getColor(this,R.color.red))
                    //    binding.option3.setBackgroundColor(ContextCompat.getColor(this,R.color.red))
//
                    //}
                    //else if(binding.option3.text==answer)
                    //{
                    //    binding.option3.setBackgroundColor(ContextCompat.getColor(this,R.color.green))
                    //    binding.option1.setBackgroundColor(ContextCompat.getColor(this,R.color.red))
                    //    binding.option2.setBackgroundColor(ContextCompat.getColor(this,R.color.red))
                    //}
                }
                binding.option2.setOnClickListener{
                    binding.option2.setBackgroundColor(ContextCompat.getColor(this, R.color.gray01))
                    binding.option1.setBackgroundColor(ContextCompat.getColor(this,
                        R.color.secondary
                    ))
                    binding.option3.setBackgroundColor(ContextCompat.getColor(this,
                        R.color.secondary
                    ))
                    id= 2
                    //if(binding.option2.text==answer)
                    //{
                    //    binding.option2.setBackgroundColor(ContextCompat.getColor(this,R.color.green))
                    //    binding.option1.setBackgroundColor(ContextCompat.getColor(this,R.color.red))
                    //    binding.option3.setBackgroundColor(ContextCompat.getColor(this,R.color.red))
                    //}
                    //else if(binding.option1.text==answer)
                    //{
                    //    binding.option1.setBackgroundColor(ContextCompat.getColor(this,R.color.green))
                    //    binding.option2.setBackgroundColor(ContextCompat.getColor(this,R.color.red))
                    //    binding.option3.setBackgroundColor(ContextCompat.getColor(this,R.color.red))
//
                    //}
                    //else if(binding.option3.text==answer)
                    //{
                    //    binding.option3.setBackgroundColor(ContextCompat.getColor(this,R.color.green))
                    //    binding.option1.setBackgroundColor(ContextCompat.getColor(this,R.color.red))
                    //    binding.option2.setBackgroundColor(ContextCompat.getColor(this,R.color.red))
                    //}
                }
                binding.option3.setOnClickListener{
                    binding.option3.setBackgroundColor(ContextCompat.getColor(this, R.color.gray01))
                    binding.option2.setBackgroundColor(ContextCompat.getColor(this,
                        R.color.secondary
                    ))
                    binding.option1.setBackgroundColor(ContextCompat.getColor(this,
                        R.color.secondary
                    ))
                    id= 3
                    //if(binding.option3.text==answer)
                    //{
                    //    binding.option3.setBackgroundColor(ContextCompat.getColor(this,R.color.green))
                    //    binding.option1.setBackgroundColor(ContextCompat.getColor(this,R.color.red))
                    //    binding.option2.setBackgroundColor(ContextCompat.getColor(this,R.color.red))
                    //}
                    //else if(binding.option1.text==answer)
                    //{
                    //    binding.option1.setBackgroundColor(ContextCompat.getColor(this,R.color.green))
                    //    binding.option2.setBackgroundColor(ContextCompat.getColor(this,R.color.red))
                    //    binding.option3.setBackgroundColor(ContextCompat.getColor(this,R.color.red))
//
                    //}
                    //else if(binding.option2.text==answer)
                    //{
                    //    binding.option2.setBackgroundColor(ContextCompat.getColor(this,R.color.green))
                    //    binding.option1.setBackgroundColor(ContextCompat.getColor(this,R.color.red))
                    //    binding.option3.setBackgroundColor(ContextCompat.getColor(this,R.color.red))
                    //}

                }
                binding.submit.setOnClickListener {



                    if(id == 1){
                        if(binding.option1.text==answer){
                            binding.option1.setBackgroundColor(ContextCompat.getColor(this,
                                R.color.green
                            ))
                        }
                        else{
                            binding.option1.setBackgroundColor(ContextCompat.getColor(this,
                                R.color.red
                            ))
                        }
                    }
                    else if(id == 2){
                        if(binding.option2.text==answer){
                            binding.option2.setBackgroundColor(ContextCompat.getColor(this,
                                R.color.green
                            ))
                        }
                        else{
                            binding.option2.setBackgroundColor(ContextCompat.getColor(this,
                                R.color.red
                            ))
                        }
                    }
                    else if(id == 3){
                        if(binding.option3.text==answer){
                            binding.option3.setBackgroundColor(ContextCompat.getColor(this,
                                R.color.green
                            ))
                        }
                        else{
                            binding.option3.setBackgroundColor(ContextCompat.getColor(this,
                                R.color.red
                            ))
                        }
                    }
                    binding.option1.isEnabled=false
                    binding.option1.isClickable=false

                    binding.option2.isEnabled=false
                    binding.option2.isClickable=false

                    binding.option3.isEnabled=false
                    binding.option3.isClickable=false

                    binding.submit.visibility=android.view.View.INVISIBLE
                    binding.submit.isEnabled=false
                    binding.next.visibility=android.view.View.VISIBLE

                    binding.next.setOnClickListener {
                        val intent= Intent(this@QuizQuestionsActivity, GameScoreActivity::class.java)
                        startActivity(intent)
                        //startActivity(Intent(this@MapsDemoActivity,ViewPlaceActivity::class.java))

                        true
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

    override fun onBackPressed() {
        if (binding.submit.isEnabled == true){
            overridePendingTransition(0,0)
            finish()
        }
    }
}