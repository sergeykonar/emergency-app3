package com.example.emergencyappnew.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

import android.widget.Toast

import android.provider.Settings
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat.PRIORITY_HIGH
import com.example.emergencyappnew.R


class LocationService : Service() {


    private val tag = LocationService::class.java.canonicalName
    private var locationManager: LocationManager? = null
    private var latitude: String? = null
    private var longitude: String? = null
    override fun onCreate() {
        super.onCreate()

    }

    private fun startForeground() {
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel("my_service", "My Background Service")
            } else {
                ""
            }

        val notificationBuilder = NotificationCompat.Builder(this, channelId )
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(PRIORITY_HIGH)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(101, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String{
        val chan = NotificationChannel(channelId,
            channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        startForeground()
        locationManager = applicationContext.getSystemService(LOCATION_SERVICE) as LocationManager
        locationManager!!.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            1000,
            1f,
            locationListener
        )

        return START_NOT_STICKY
    }

    private val locationListener = object : LocationListener{
        override fun onLocationChanged(location: Location) {
            Log.d(tag, String.format("Location updated: %2f %2f", location.latitude, location.latitude))
            latitude = location.latitude.toString()
            longitude = location.longitude.toString()
            insertCoordinates(latitude!!, longitude!!)
            val i = Intent("location_update")
            i.putExtra("latitude", latitude)
            i.putExtra("longitude", longitude)
            sendBroadcast(i)
        }

        override fun onProviderDisabled(provider: String) {
            val i = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (locationManager != null) locationManager!!.removeUpdates(locationListener!!)
    }

    private fun insertCoordinates(latitude: String, longitude: String) {
        Toast.makeText(this@LocationService, "Coordinates Inserted $latitude $longitude", Toast.LENGTH_SHORT)
            .show()
    }

    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}