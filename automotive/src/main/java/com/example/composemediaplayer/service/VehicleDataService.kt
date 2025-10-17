package com.example.composemediaplayer.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteCallbackList
import android.util.Log
import com.example.composemediaplayer.ivehicle.IVehicleDataCallback
import com.example.composemediaplayer.ivehicle.IVehicleDataService
import kotlinx.coroutines.*
import kotlin.random.Random

class VehicleDataService : Service() {

    private val callbackList = RemoteCallbackList<IVehicleDataCallback>()

    private var currentSpeed = 0f
    private var batteryLevel = 100f

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val binder = object : IVehicleDataService.Stub() {
        override fun getCurrentSpeed(): Float = currentSpeed

        override fun getBatteryLevel(): Float = batteryLevel

        override fun registerCallback(callback: IVehicleDataCallback?) {
            if (callback != null) callbackList.register(callback)
        }

        override fun unregisterCallback(callback: IVehicleDataCallback?) {
            if (callback != null) callbackList.unregister(callback)
        }
    }

    override fun onCreate() {
        super.onCreate()
        batteryLevel = Random.nextFloat() * 20f + 80f
        currentSpeed = Random.nextFloat() * 120f

        // Speed updates
        serviceScope.launch {
            while (isActive) {
                currentSpeed = Random.nextFloat() * 120f
                synchronized(callbackList) {
                    val count = callbackList.beginBroadcast()
                    for (i in 0 until count) {
                        try {
                            callbackList.getBroadcastItem(i).onBatteryLevelChanged(batteryLevel)
                        } catch (_: Exception) { }
                    }
                    callbackList.finishBroadcast()
                }

                delay(1000)
            }
        }

        serviceScope.launch {
            var isFirst = true

            while (isActive) {
                if (isFirst) {
                    isFirst = false
                } else {
                    batteryLevel -= Random.nextFloat() * 2f
                    batteryLevel = batteryLevel.coerceAtLeast(0f)
                }

                synchronized(callbackList) {
                    val count = callbackList.beginBroadcast()
                    for (i in 0 until count) {
                        try {
                            callbackList.getBroadcastItem(i).onSpeedChanged(currentSpeed)
                        } catch (_: Exception) { }
                    }
                    callbackList.finishBroadcast()
                }

                delay(3000)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel() // Cancel coroutine loop
    }

    override fun onBind(intent: Intent?): IBinder = binder
}
