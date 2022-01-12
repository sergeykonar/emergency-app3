package com.example.emergencyappnew

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.emergencyappnew.api.RetroClient
import com.example.emergencyappnew.service.LocationService
import com.example.emergencyappnew.service.MyFirebaseMessagingService
import com.example.emergencyappnew.util.Constants
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import com.utsman.samplegooglemapsdirection.kotlin.model.DirectionResponses
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private val REQUEST_CODE_LOCATION_PERMISSION = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initNavigation()
        checkPermissions()
        initPush()
    }

    private fun initPush(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            Firebase.messaging.isAutoInitEnabled = true
            if (!task.isSuccessful) {
                Log.e("FCM", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            val token = task.result

            Log.d("TOKEN", token!!)

        })
        val intent = Intent(this, MyFirebaseMessagingService::class.java)

        startForegroundService(intent)
    }


    private fun initNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            .setupWithNavController(navController)
    }

    private fun checkPermissions(){
        val permList = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION)

        if(ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this, permList, REQUEST_CODE_LOCATION_PERMISSION)
        }else{
            startLocationService()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.size > 0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startLocationService()
            }else{
                Log.e("RR", "denied")
            }
        }
    }

    private fun startLocationService(){
        val intent = Intent(applicationContext, LocationService::class.java)
        intent.action = Constants.ACTION_START_LOCATION_SERVICE
        applicationContext.startForegroundService(intent)
    }
}