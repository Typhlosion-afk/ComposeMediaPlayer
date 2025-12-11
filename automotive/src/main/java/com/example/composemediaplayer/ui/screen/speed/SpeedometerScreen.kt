package com.example.composemediaplayer.ui.screen.speed

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composemediaplayer.R
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.composemediaplayer.ui.MainViewModel
import com.example.composemediaplayer.ui.childview.EnergyMeter
import com.example.composemediaplayer.ui.childview.Speedometer
import kotlin.math.log

@Composable
fun SpeedometerScreen(viewModel: VehicleStatusViewModel, mainViewModel: MainViewModel) {
    val speed by viewModel.speed.collectAsStateWithLifecycle()
    val energyLevel by viewModel.energyLevel.collectAsStateWithLifecycle()
    val isCharging by viewModel.isCharging.collectAsStateWithLifecycle()
    val useMph by mainViewModel.isMphEnabled.collectAsStateWithLifecycle()
    val isDarkMode by mainViewModel.isDarkModeEnabled.collectAsStateWithLifecycle()

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceAround
    ) {

        // --- SPEEDOMETER ---
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


        // --- ICON CHARGING ---
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            androidx.compose.material3.Icon(
                painter = painterResource(
                    id = if (isCharging) R.drawable.ic_charging else R.drawable.ic_not_charging
                ),
                contentDescription = "Charging status",
                modifier = Modifier.size(80.dp),
                tint = Color.Unspecified
            )
        }

        // --- EnergyMeter ---
        EnergyMeter(
            isDarkMode = isDarkMode,
            currentLevel = energyLevel,
            unitLabel = "%",
            modifier = Modifier.size(400.dp)
        )
    }
}
