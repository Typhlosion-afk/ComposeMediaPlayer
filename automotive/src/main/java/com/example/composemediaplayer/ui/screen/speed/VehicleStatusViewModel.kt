package com.example.composemediaplayer.ui.screen.speed

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.ViewModel
import com.example.composemediaplayer.ivehicle.IVehicleDataCallback
import com.example.composemediaplayer.ivehicle.IVehicleDataService
import com.example.composemediaplayer.remote.VehicleDataServiceAdapter
import com.example.composemediaplayer.remote.VehicleDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class VehicleStatusViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val dataSource: VehicleDataSource = VehicleDataServiceAdapter(context)

    val speed = dataSource.speedFlow
    val energyLevel = dataSource.batteryFlow

    init {
        dataSource.connect()
    }

    override fun onCleared() {
        dataSource.disconnect()
        super.onCleared()
    }
}
