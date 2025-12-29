package com.example.composemediaplayer.remote

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.example.ivehicle.aidl.IVehicleDataCallback
import com.example.ivehicle.aidl.IVehicleDataService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class VehicleDataServiceAdapter(
    private val context: Context
) : VehicleDataSource {
    private var isBound = false
    private var vehicleService: IVehicleDataService? = null

    private val _speed = MutableStateFlow(0f)
    override val speedFlow: StateFlow<Float> = _speed

    private val _battery = MutableStateFlow(0f)
    override val batteryFlow: StateFlow<Float> = _battery

    private val _isCharging = MutableStateFlow(false)
    override val isChargingFlow: StateFlow<Boolean> = _isCharging

    private val callback = object : IVehicleDataCallback.Stub() {
        override fun onSpeedChanged(speed: Float) {
            _speed.value = speed
            Log.d("VehicleAdapter", "onSpeedChanged: $speed")
        }

        override fun onBatteryLevelChanged(level: Float) {
            _battery.value = level
            Log.d("VehicleAdapter", "onBatteryLevelChanged: $level")
        }

        override fun onChargingStateChanged(charging: Boolean) {
            _isCharging.value = charging
            Log.d("VehicleAdapter", "onChargingStateChanged: $charging")
        }
    }

    private val connection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            Log.d("VehicleAdapter", "Service connected: $name")
            vehicleService = IVehicleDataService.Stub.asInterface(binder)
            vehicleService?.registerCallback(callback)
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.w("VehicleAdapter", "Service disconnected: $name")
            vehicleService = null
            isBound = false
        }
    }

    override fun connect() {
        val packageName = "com.example.vehicleservice"
        val className = "com.example.vehicleservice.service.VehicleDataService"
        Log.d("VehicleAdapter", "Trying to bind to $packageName/$className")

        val intent = Intent()
        intent.component = ComponentName(packageName, className)
        val bound = context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        Log.d("VehicleAdapter", "bindService called, result = $bound")
    }

    override fun disconnect() {
        vehicleService?.unregisterCallback(callback)
        context.unbindService(connection)
    }
}
