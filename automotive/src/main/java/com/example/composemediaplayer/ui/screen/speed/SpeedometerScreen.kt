package com.example.composemediaplayer.ui.screen.speed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.composemediaplayer.ui.MainViewModel
import com.example.composemediaplayer.ui.childview.EnergyMeter
import com.example.composemediaplayer.ui.childview.Speedometer

@Composable
fun SpeedometerScreen(viewModel: VehicleStatusViewModel, mainViewModel: MainViewModel) {
    val speed by viewModel.speed.collectAsStateWithLifecycle()
    val energyLevel by viewModel.energyLevel.collectAsStateWithLifecycle()
    val useMph by mainViewModel.isMphEnabled.collectAsStateWithLifecycle()
    val isDarkMode by mainViewModel.isDarkModeEnabled.collectAsStateWithLifecycle()

    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Speedometer(
                currentSpeed = speed,
                useMph = useMph,
                modifier = Modifier.size(400.dp)
            )
            Text(
                text = if (useMph) "MPH" else "Km/h",
                fontSize = 24.sp
            )
        }

        EnergyMeter(
            isDarkMode = isDarkMode,
            currentLevel = energyLevel,
            unitLabel = "%",
            modifier = Modifier.size(400.dp)
        )
    }
}
