package com.example.emergencyappnew

import android.app.Application
import android.content.Intent
import android.os.Build
import com.example.emergencyappnew.service.MyFirebaseMessagingService

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        val intent = Intent(this, MyFirebaseMessagingService::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }
}