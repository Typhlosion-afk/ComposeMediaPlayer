package com.example.composemediaplayer.ui.screen.speed

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.ViewModel
import com.example.composemediaplayer.ivehicle.IVehicleDataCallback
import com.example.composemediaplayer.ivehicle.IVehicleDataService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class VehicleStatusViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private var vehicleService: IVehicleDataService? = null

    private val _speed = MutableStateFlow(0f)
    val speed: StateFlow<Float> = _speed

    private val _energyLevel = MutableStateFlow(0f)
    val energyLevel: StateFlow<Float> = _energyLevel

    private val callback = object : IVehicleDataCallback.Stub() {
        override fun onSpeedChanged(speed: Float) {
            _speed.value = speed
        }
        override fun onBatteryLevelChanged(level: Float) {
            _energyLevel.value = level
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

    init {
        val intent = Intent("com.example.ACTION_BIND_VEHICLE_SERVICE")
        intent.setPackage("com.example.composemediaplayer")
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onCleared() {
        vehicleService?.unregisterCallback(callback)
        context.unbindService(connection)
        super.onCleared()
    }
}
