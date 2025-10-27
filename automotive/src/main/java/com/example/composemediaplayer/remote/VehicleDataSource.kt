package com.example.composemediaplayer.remote

import kotlinx.coroutines.flow.StateFlow

interface VehicleDataSource {
    val speedFlow: StateFlow<Float>
    val batteryFlow: StateFlow<Float>

    fun connect()
    fun disconnect()
}