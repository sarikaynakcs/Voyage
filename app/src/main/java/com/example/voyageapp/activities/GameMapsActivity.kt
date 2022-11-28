package com.example.voyageapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.voyageapp.Common.Common
import com.example.voyageapp.Model.MyPlaces
import com.example.voyageapp.R
import com.example.voyageapp.Remote.IGoogleAPIService
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.voyageapp.databinding.ActivityGameMapsBinding
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_game_maps.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class GameMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var myMarker: Marker
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityGameMapsBinding
    private lateinit var firebaseAuth: FirebaseAuth

    //
    private var latitude:Double=0.toDouble()
    private var longitude:Double=0.toDouble()
    private lateinit var mLastLocation: Location
    private var mMarker:Marker?=null


    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: com.google.android.gms.location.LocationRequest
    lateinit var locationCallback: LocationCallback
    companion object{
        const val MY_PERMISSION_CODE: Int = 2000 //1000
    }

    lateinit var mServices: IGoogleAPIService
    internal lateinit var currentPlaces: MyPlaces

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        firebaseAuth = FirebaseAuth.getInstance()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        //init service
        mServices = Common.googleAPIService

        //..........................

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkLocationPermission()){
                buildLocationRequest()
                buildLocationCallback()

                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
                fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper())
            }

        }
        else{
            buildLocationRequest()
            buildLocationCallback()

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper())
        }

        bottom_navigation_view.setOnNavigationItemSelectedListener { item->
            when(item.itemId)
            {
                R.id.action_museum -> nearByPlace("Museum")


            }
            true
        }
    }

    private fun nearByPlace(typePlace: String) {

        //Clear all marker on Map
        mMap.clear()
        //build URL request base on location
        val url=getUrl(latitude,longitude,typePlace)

        mServices.getNearbyPlaces(url)
            .enqueue(object : Callback<MyPlaces>{
                @SuppressLint("PotentialBehaviorOverride")
                override fun onResponse(call: Call<MyPlaces>, response: Response<MyPlaces>) {
                    currentPlaces = response.body()!!

                    if (response.isSuccessful)
                    {
                        for(i in 0 until response.body()!!.results!!.size)
                        {
                            val markerOptions=MarkerOptions()
                            val googlePlace = response.body()!!.results!![i]
                            val lat = googlePlace.geometry!!.location!!.lat
                            val lng = googlePlace.geometry!!.location!!.lng
                            val placeName = googlePlace.name
                            val latLng= LatLng(lat,lng)

                            val mRef = FirebaseDatabase.getInstance().getReference("GameUpdate")
                            mRef.addValueEventListener(object : ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.hasChild(placeName!!)) {
                                        markerOptions.position(latLng)
                                        markerOptions.title(placeName)
                                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_museum))
                                        markerOptions.snippet(i.toString())

                                        //Add marker to map
                                        mMap.addMarker(markerOptions)
                                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                                        mMap.animateCamera(CameraUpdateFactory.zoomTo(15f))
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }

                            })
                            //markerOptions.position(latLng)
                            //markerOptions.title(placeName)
                            //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_museum))
                            //markerOptions.snippet(i.toString())
                            //Add marker to map
                            //mMap.addMarker(markerOptions)
                            //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                            //mMap.animateCamera(CameraUpdateFactory.zoomTo(15f))

                        }
                        mMap!!.setOnInfoWindowClickListener  { marker ->

                            val mRef = FirebaseDatabase.getInstance().getReference("PlayerGames").child(firebaseAuth.uid!!)
                            mRef.addValueEventListener(object : ValueEventListener{
                                    override fun onDataChange(snapshot: DataSnapshot) {

                                        if (snapshot.hasChild("games")) {
                                            mRef.child("games")
                                                .addValueEventListener(object : ValueEventListener{
                                                    override fun onDataChange(snapshot: DataSnapshot) {
                                                        for (sd in snapshot.children) {
                                                            val museumName = sd.key

                                                            if (museumName == marker.title) {
                                                                val intent = Intent(this@GameMapsActivity, GamePrizeActivity::class.java)
                                                                Toast.makeText(this@GameMapsActivity, "Keşfet sayfasına yönlendiriliyorsun", Toast.LENGTH_SHORT).show()
                                                                intent.putExtra("museum", marker.title)
                                                                startActivity(intent)
                                                                finish()
                                                                overridePendingTransition(0,0)
                                                                mRef.removeEventListener(this)
                                                            }
                                                            else if (marker.title != "Your Position") {
                                                                val intent = Intent(this@GameMapsActivity, InsideGameActivity::class.java)
                                                                intent.putExtra("museum", marker.title)
                                                                startActivity(intent)
                                                                //finish()
                                                                overridePendingTransition(0,0)
                                                                mRef.removeEventListener(this)
                                                            }
                                                        }
                                                    }

                                                    override fun onCancelled(error: DatabaseError) {
                                                        TODO("Not yet implemented")
                                                    }

                                                })
                                        }
                                        else {
                                            val intent = Intent(this@GameMapsActivity, InsideGameActivity::class.java)
                                            intent.putExtra("museum", marker.title)
                                            startActivity(intent)
                                            //finish()
                                            overridePendingTransition(0,0)
                                            mRef.removeEventListener(this)
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }

                                })
                        }

                    }

                }

                override fun onFailure(call: Call<MyPlaces>, t: Throwable) {
                    Toast.makeText(baseContext,""+ t.message,Toast.LENGTH_SHORT).show()
                }

            })

    }

    private fun getUrl(latitude: Double, longitude: Double, typePlace: String): String {
        val googlePlaceUrl = StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json")
        googlePlaceUrl.append("?location=39.87523, 32.86346")
        googlePlaceUrl.append("&radius=10000") //10km
        googlePlaceUrl.append("&types=museum")
        googlePlaceUrl.append("&sensor=true")
        googlePlaceUrl.append("&key=AIzaSyD4juMI81htuZG65dWSd2osD9WwtdmkdH4")

        Log.d("URL_DEBUG",googlePlaceUrl.toString())
        return googlePlaceUrl.toString()

    }

    private fun buildLocationCallback() {
        locationCallback = object : LocationCallback(){
            override fun onLocationResult(p0: LocationResult) {
                mLastLocation = p0!!.locations.get(p0!!.locations.size-1)

                if (mMarker!= null)
                {
                    mMarker!!.remove()
                }
                latitude = mLastLocation.latitude
                longitude = mLastLocation.longitude

                val latLng = LatLng(latitude,longitude)
                val markerOptions=MarkerOptions()
                    .position(latLng)
                    .title("Your Position")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                mMarker= mMap!!.addMarker(markerOptions)

                mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                mMap!!.animateCamera(CameraUpdateFactory.zoomTo(11f))

            }
        }
    }

    private fun buildLocationRequest() {
        locationRequest = com.google.android.gms.location.LocationRequest()
        locationRequest.priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval=60000
        locationRequest.fastestInterval = 5000
        locationRequest.smallestDisplacement =10f
    }

    private fun checkLocationPermission(): Boolean {
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.ACCESS_FINE_LOCATION))
                ActivityCompat.requestPermissions(this, arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ), MY_PERMISSION_CODE
                )
            else
                ActivityCompat.requestPermissions(this, arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ), MY_PERMISSION_CODE
                )
            return false
        }
        else
            return true
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode)
        {
            MY_PERMISSION_CODE ->{
                if(grantResults.size >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
                        if(checkLocationPermission()) {
                            buildLocationRequest()
                            buildLocationCallback()

                            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
                            fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper())
                            mMap.isMyLocationEnabled = true
                        }
                }
                else{
                    Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    override fun onStop() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        super.onStop()
    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                mMap!!.isMyLocationEnabled=true
            }
        }
        else
            mMap!!.isMyLocationEnabled=true

    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        startActivity(Intent(this@GameMapsActivity, GameActivity::class.java))
        overridePendingTransition(0,0)
        finish()
    }
}