package com.example.composemediaplayer.ui.screen.setting

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composemediaplayer.ui.MainViewModel

@Composable
fun SettingsScreen(
    viewModel: MainViewModel = viewModel()
) {
    val isDarkMode by viewModel.isDarkModeEnabled.collectAsState()
    val isMphEnabled by viewModel.isMphEnabled.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SettingSwitch(
            title = "Dark Mode",
            isChecked = isDarkMode,
            onCheckedChange = { newCheckedState ->
                // Send the event to the ViewModel
                viewModel.setDarkMode(newCheckedState)
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        SettingSwitch(
            title = "Use Miles per hour (MPH)",
            isChecked = isMphEnabled,
            onCheckedChange = { newCheckedState ->
                // Send the event to the ViewModel
                viewModel.setUseMph(newCheckedState)
            }
        )
    }
}

@Composable
private fun SettingSwitch(
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title)
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}
