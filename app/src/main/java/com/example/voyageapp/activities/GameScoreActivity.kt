package com.example.voyageapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.example.voyageapp.R
import com.example.voyageapp.databinding.ActivityGameScoreBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class GameScoreActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameScoreBinding

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameScoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        firebaseAuth = FirebaseAuth.getInstance()

        val check = intent.getStringExtra("check")
        val museum = intent.getStringExtra("museum")

        //adding parameter to firebase database
        if (check != "false") {
            binding.background.setBackgroundResource(R.drawable.conc)
            binding.winText.setText("TEBRİKLER")
            binding.firstText.setText("Devam ederek oyunu oynayan insanlarla etkileşime geçebilirsin")
            binding.secondText.setText("Ya da ana menüye dönerek yeni oyunlar oynayabilirsin. Unutma bu müzedeki ilerlemen silinmeyecek, tekrar girdiğinde doğrudan etkileşime geçebileceğin insanları görebileceksin")
            binding.nextButton.visibility=android.view.View.VISIBLE
            binding.mainMenuButton.visibility=android.view.View.VISIBLE
            val ref = FirebaseDatabase.getInstance().getReference("PlayerGames")
            ref.child(firebaseAuth.uid!!).child("games").child(museum!!).setValue(true)
        }
        else {
            binding.background.setBackgroundResource(R.drawable.fail)
            binding.winText.setText("BAŞARAMADIN")
            binding.firstText.setText("Ana Menüye dönerek tekrar oyuna girip görevi tamamlayabilirsin")
            binding.secondText.setText("")
            binding.loseMainMenuButton.visibility=android.view.View.VISIBLE
        }


        binding.mainMenuButton.setOnClickListener {
            val intent = Intent(this@GameScoreActivity, GameActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(0,0)
        }

        binding.nextButton.setOnClickListener {
            val intent = Intent(this@GameScoreActivity, GamePrizeActivity::class.java)
            intent.putExtra("museum", museum)
            startActivity(intent)
            finish()
            overridePendingTransition(0,0)
        }

        binding.loseMainMenuButton.setOnClickListener {
            val intent = Intent(this@GameScoreActivity, GameActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(0,0)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        startActivity(Intent(this@GameScoreActivity, GameActivity::class.java))
        overridePendingTransition(0,0)
        finish()
    }
}