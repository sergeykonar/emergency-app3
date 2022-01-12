package com.example.emergencyappnew.service

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class MyWorker(val context: Context, private val workerParameters: WorkerParameters) : Worker(context, workerParameters) {
    override fun doWork(): Result {
        Log.d("SERVICE", "Performing long running task in scheduled job")

        return Result.success()
    }

}