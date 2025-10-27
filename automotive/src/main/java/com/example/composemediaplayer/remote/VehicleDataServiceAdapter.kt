package com.example.composemediaplayer.remote

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.example.composemediaplayer.ivehicle.IVehicleDataCallback
import com.example.composemediaplayer.ivehicle.IVehicleDataService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class VehicleDataServiceAdapter(
    private val context: Context
) : VehicleDataSource {

    private var vehicleService: IVehicleDataService? = null

    private val _speed = MutableStateFlow(0f)
    override val speedFlow: StateFlow<Float> = _speed

    private val _battery = MutableStateFlow(0f)
    override val batteryFlow: StateFlow<Float> = _battery

    private val callback = object : IVehicleDataCallback.Stub() {
        override fun onSpeedChanged(speed: Float) {
            _speed.value = speed
        }

        override fun onBatteryLevelChanged(level: Float) {
            _battery.value = level
        }
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            vehicleService = IVehicleDataService.Stub.asInterface(binder)
            vehicleService?.registerCallback(callback)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            vehicleService = null
        }
    }

    override fun connect() {
        val intent = Intent("com.example.ACTION_BIND_VEHICLE_SERVICE")
        intent.setPackage("com.example.composemediaplayer")
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun disconnect() {
        vehicleService?.unregisterCallback(callback)
        context.unbindService(connection)
    }
}
