package com.example.emergencyappnew.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetroClient {

    private val baseUrl = "https://maps.googleapis.com/maps/api/directions/"

    private fun getInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getRetrofitService(): DirectionsApi {
        return getInstance().create(DirectionsApi::class.java)
    }


}