package com.example.composemediaplayer.ui.screen.speed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.composemediaplayer.ui.childview.EnergyMeter
import com.example.composemediaplayer.ui.childview.Speedometer

@Composable
fun SpeedometerScreen(viewModel: VehicleStatusViewModel) {
    val speed by viewModel.speed.collectAsStateWithLifecycle()
    val energyLevel by viewModel.energyLevel.collectAsStateWithLifecycle()

    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Speedometer(
            currentSpeed = speed,
            tickStep = 20f,
            modifier = Modifier.size(400.dp)
        )

        EnergyMeter(
            currentLevel = energyLevel,
            unitLabel = "%",
            modifier = Modifier.size(400.dp)
        )
    }
}
