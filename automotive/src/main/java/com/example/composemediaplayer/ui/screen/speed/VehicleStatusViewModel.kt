package com.example.composemediaplayer.ui.screen.speed

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.composemediaplayer.remote.VehicleDataServiceAdapter
import com.example.composemediaplayer.remote.VehicleDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class VehicleStatusViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val dataSource: VehicleDataSource = VehicleDataServiceAdapter(context)

    val speed = dataSource.speedFlow
    val energyLevel = dataSource.batteryFlow
    val isCharging = dataSource.isChargingFlow

    init {
        dataSource.connect()
    }

    override fun onCleared() {
        dataSource.disconnect()
        super.onCleared()
    }
}
