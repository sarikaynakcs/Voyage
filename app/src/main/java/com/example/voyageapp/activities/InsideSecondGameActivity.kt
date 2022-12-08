package com.example.voyageapp.activities

import android.content.ClipData
import android.content.ClipDescription
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.DragEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.voyageapp.R
import com.example.voyageapp.databinding.ActivityInsideSecondGameBinding
import com.example.voyageapp.databinding.ActivityInsideThirdGameBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_inside_second_game.*

class InsideSecondGameActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    var id=0
    private lateinit var timer: CountDownTimer

    private lateinit var binding: ActivityInsideSecondGameBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityInsideSecondGameBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        val long = intent.getStringExtra("museum")
        readData("$long")

        timer = object : CountDownTimer(100000,1000) {
            override fun onFinish() {
                binding.sayac.text = "Kalan Zaman: 0"
                var score = intent.getStringExtra("score")
                //checkAnswer = false
                val intent=Intent(this@InsideSecondGameActivity,InsideThirdGameActivity::class.java)
                //intent.putExtra("check",checkAnswer.toString())
                intent.putExtra("score", (score).toString())
                startActivity(intent)
                finish()
                overridePendingTransition(0,0)
            }

            override fun onTick(millisUntilFinished: Long) {

                binding.sayac.text = "Kalan Zaman: ${millisUntilFinished/1000}"
            }

        }.start()
    }

    private fun readData(name: String) {
        database = FirebaseDatabase.getInstance().getReference("GameUpdate")
        database.child(name).child("examples").child("exampleImage").get().addOnSuccessListener {
            if (it.exists()) {
                val image1 = it.child("image1").value
                val image2 = it.child("image2").value
                val image3 = it.child("image3").value
                var option1=it.child("option1").value
                var option2=it.child("option2").value
                var option3=it.child("option3").value
                var answer1=it.child("answer1").value
                var answer2=it.child("answer2").value
                var answer3=it.child("answer3").value
                binding.text1.text=option1.toString()
                binding.text2.text=option2.toString()
                binding.text3.text=option3.toString()
                Glide.with(this@InsideSecondGameActivity)
                    .load(image1)
                    .placeholder(R.drawable.ic_person_gray)
                    .into(findViewById(R.id.image1))
                Glide.with(this@InsideSecondGameActivity)
                    .load(image2)
                    .placeholder(R.drawable.ic_person_gray)
                    .into(findViewById(R.id.image2))
                Glide.with(this@InsideSecondGameActivity)
                    .load(image3)
                    .placeholder(R.drawable.ic_person_gray)
                    .into(findViewById(R.id.image3))

                var score = intent.getStringExtra("score")

                binding.time.text = score


                binding.text1.setOnClickListener{
                    binding.text1.setBackgroundResource(R.drawable.options_selected)
                    //binding.time.text="10"
                    binding.imageText1.setOnClickListener{
                        binding.imageText1.isClickable=false
                        binding.imageText2.isClickable=false
                        binding.imageText3.isClickable=false
                        //
                        binding.imageText1.text= option1.toString()
                        binding.imageText1.setBackgroundResource(R.drawable.museum_name_shape)
                        binding.text1.setBackgroundResource(R.drawable.options_selected)
                    }
                    binding.imageText2.setOnClickListener{
                        binding.imageText1.isClickable=false
                        binding.imageText2.isClickable=false
                        binding.imageText3.isClickable=false
                        //
                        binding.imageText2.text= option1.toString()
                        binding.imageText2.setBackgroundResource(R.drawable.museum_name_shape)
                        binding.text1.setBackgroundResource(R.drawable.options_selected)
                    }
                    binding.imageText3.setOnClickListener{
                        binding.imageText1.isClickable=false
                        binding.imageText2.isClickable=false
                        binding.imageText3.isClickable=false
                        //
                        binding.imageText3.text= option1.toString()
                        binding.imageText3.setBackgroundResource(R.drawable.museum_name_shape)
                        binding.text1.setBackgroundResource(R.drawable.options_selected)
                    }
                }
                binding.text2.setOnClickListener{
                    binding.text2.setBackgroundResource(R.drawable.options_selected)
                    binding.imageText1.setOnClickListener{
                        binding.imageText1.isClickable=false
                        binding.imageText2.isClickable=false
                        binding.imageText3.isClickable=false
                        //
                        binding.imageText1.text= option2.toString()
                        binding.imageText1.setBackgroundResource(R.drawable.museum_name_shape)
                        binding.text2.setBackgroundResource(R.drawable.options_selected)
                    }
                    binding.imageText2.setOnClickListener{
                        binding.imageText1.isClickable=false
                        binding.imageText2.isClickable=false
                        binding.imageText3.isClickable=false
                        //
                        binding.imageText2.text= option2.toString()
                        binding.imageText2.setBackgroundResource(R.drawable.museum_name_shape)
                        binding.text2.setBackgroundResource(R.drawable.options_selected)
                    }
                    binding.imageText3.setOnClickListener{
                        binding.imageText1.isClickable=false
                        binding.imageText2.isClickable=false
                        binding.imageText3.isClickable=false
                        //
                        binding.imageText3.text= option2.toString()
                        binding.imageText3.setBackgroundResource(R.drawable.museum_name_shape)
                        binding.text2.setBackgroundResource(R.drawable.options_selected)
                    }
                }
                binding.text3.setOnClickListener{
                    binding.text3.setBackgroundResource(R.drawable.options_selected)
                    binding.imageText1.setOnClickListener{
                        binding.imageText1.isClickable=false
                        binding.imageText2.isClickable=false
                        binding.imageText3.isClickable=false
                        //
                        binding.imageText1.text= option3.toString()
                        binding.imageText1.setBackgroundResource(R.drawable.museum_name_shape)
                        binding.text3.setBackgroundResource(R.drawable.options_selected)
                    }
                    binding.imageText2.setOnClickListener{
                        binding.imageText1.isClickable=false
                        binding.imageText2.isClickable=false
                        binding.imageText3.isClickable=false
                        //
                        binding.imageText2.text= option3.toString()
                        binding.imageText2.setBackgroundResource(R.drawable.museum_name_shape)
                        binding.text3.setBackgroundResource(R.drawable.options_selected)
                    }
                    binding.imageText3.setOnClickListener{
                        binding.imageText1.isClickable=false
                        binding.imageText2.isClickable=false
                        binding.imageText3.isClickable=false
                        //
                        binding.imageText3.text= option3.toString()
                        binding.imageText3.setBackgroundResource(R.drawable.museum_name_shape)
                        binding.text3.setBackgroundResource(R.drawable.options_selected)
                    }
                }
                binding.clear.setOnClickListener{
                    binding.imageText1.text=""
                    binding.imageText2.text=""
                    binding.imageText3.text=""
                    binding.imageText1.setBackgroundResource(R.drawable.empty_space)
                    binding.imageText2.setBackgroundResource(R.drawable.empty_space)
                    binding.imageText3.setBackgroundResource(R.drawable.empty_space)
                    binding.text1.setBackgroundResource(R.drawable.options_unselected)
                    binding.text2.setBackgroundResource(R.drawable.options_unselected)
                    binding.text3.setBackgroundResource(R.drawable.options_unselected)

                }

                binding.imageButton.setOnClickListener{
                    id=1
                    timer.cancel()
                    val intent=Intent(this@InsideSecondGameActivity,InsideThirdGameActivity::class.java)
                    if(binding.imageText1.text==answer1 && binding.imageText2.text==answer2 && binding.imageText3.text==answer3){
                        intent.putExtra("score", (score!!.toInt()+5).toString())
                    }
                    else{
                        intent.putExtra("score", (score!!.toInt()).toString())
                    }
                    intent.putExtra("museum", name)
                    startActivity(intent)
                    finish()
                    overridePendingTransition(0,0)

                }
            }
        }
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