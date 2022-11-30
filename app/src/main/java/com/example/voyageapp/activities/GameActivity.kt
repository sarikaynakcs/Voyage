package com.example.voyageapp.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.example.voyageapp.R
import com.example.voyageapp.adapters.OnBoardingViewPagerAdapter
import com.example.voyageapp.databinding.ActivityGameBinding
import com.example.voyageapp.models.OnBoardingData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout

class GameActivity : AppCompatActivity() {

    var onBoardingViewPagerAdapter: OnBoardingViewPagerAdapter? = null
    var tabLayout: TabLayout?=null
    var onBoardingViewPager: ViewPager?= null
    var next: TextView?= null
    var position=0
    var sharedPreferences: SharedPreferences?= null

    private lateinit var navigationView: BottomNavigationView

    private lateinit var binding: ActivityGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tabLayout = findViewById(R.id.tab_indicator)
        next = findViewById(R.id.next)

        if(restorePrefData()){
            binding.screenPager.visibility = View.GONE
            binding.next.visibility = View.GONE
            binding.tabIndicator.visibility = View.GONE
            binding.belowLl.visibility = View.VISIBLE
        }
        val onBoardingData:MutableList<OnBoardingData> = ArrayList()
        onBoardingData.add(OnBoardingData("","Öncelikle oynamak istediğin mekana tıkla",R.drawable.location_voyage))
        onBoardingData.add(OnBoardingData("","İpuçlarını kullanarak DOĞRU eseri bulmayı amaçlayacaksın",R.drawable.think_voyage))
        onBoardingData.add(OnBoardingData("","Bunları yaparken süreni doğru kullanmayı UNUTMA",R.drawable.time_voyage))

        setOnBoardingViewPagerAdapter(onBoardingData)
        position = onBoardingViewPager!!.currentItem
        next?.setOnClickListener{
            if (position< onBoardingData.size){
                position++
                onBoardingViewPager!!.currentItem=position
            }
            if(position==onBoardingData.size){
                savePrefData()
                binding.screenPager.visibility = View.GONE
                binding.next.visibility = View.GONE
                binding.tabIndicator.visibility = View.GONE
                binding.belowLl.visibility = View.VISIBLE
            }
        }
        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                position=tab!!.position
                if (tab.position==onBoardingData.size-1){
                    next!!.text="Başla"
                }
                else{
                    next!!.text="İlerle"
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.nextBtn.setOnClickListener {
            val intent = Intent(this@GameActivity, GameMapsActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(0,0)
        }


        window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        navigationView = findViewById(R.id.linearBotIdGame)

        navigationView.selectedItemId = R.id.nav_game

        // Perform item selected listener
        navigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_game -> return@OnNavigationItemSelectedListener true

                R.id.nav_information -> {
                    startActivity(Intent(applicationContext, DashboardUserActivity::class.java))
                    finish()
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_chat -> {
                    startActivity(Intent(applicationContext, ChatActivity::class.java))
                    finish()
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(applicationContext, ProfileActivity::class.java))
                    finish()
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }

    private fun setOnBoardingViewPagerAdapter(onBoardingData: List<OnBoardingData>){
        onBoardingViewPager=findViewById(R.id.screenPager)
        onBoardingViewPagerAdapter=OnBoardingViewPagerAdapter(this,onBoardingData)
        onBoardingViewPager!!.adapter =onBoardingViewPagerAdapter
        tabLayout?.setupWithViewPager(onBoardingViewPager)
    }
    private fun savePrefData(){
        sharedPreferences=applicationContext.getSharedPreferences("pref", Context.MODE_PRIVATE)
        val editor = sharedPreferences!!.edit()
        editor.putBoolean("isFirstTimeRun",true)
        editor.apply()
    }
    private fun restorePrefData(): Boolean{
        sharedPreferences=applicationContext.getSharedPreferences("pref", Context.MODE_PRIVATE)
        return sharedPreferences!!.getBoolean("isFirstTimeRun",false)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        startActivity(Intent(this@GameActivity, DashboardUserActivity::class.java))
        overridePendingTransition(0,0)
        finishAffinity()
        finish()
    }
}