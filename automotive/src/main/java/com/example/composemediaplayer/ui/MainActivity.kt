package com.example.composemediaplayer.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.composemediaplayer.R
import com.example.composemediaplayer.core.AppDestinations
import com.example.composemediaplayer.ui.childview.BottomNavItem
import com.example.composemediaplayer.ui.screen.NowPlayingScreen
import com.example.composemediaplayer.ui.screen.SongListScreen
import com.example.composemediaplayer.ui.screen.setting.SettingsScreen
import com.example.composemediaplayer.ui.screen.speed.SpeedometerScreen
import com.example.composemediaplayer.ui.screen.speed.VehicleStatusViewModel
import com.example.composemediaplayer.ui.theme.ComposeMediaPlayerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    private val vehicleStatusViewModel: VehicleStatusViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        askForPermission()
        setContent {
            val useDarkTheme by viewModel.isDarkModeEnabled.collectAsStateWithLifecycle()
            ComposeMediaPlayerTheme(
                darkTheme = useDarkTheme
            ) {
                val navController = rememberNavController()
                val navItems = listOf(
                    BottomNavItem.SongList,
                    BottomNavItem.Speedometer,
                    BottomNavItem.Setting,
                )

                Surface(color = MaterialTheme.colorScheme.background) {
                    Row {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentRoute = navBackStackEntry?.destination?.route

                        NavigationRail(
                            modifier = Modifier.fillMaxHeight(),
                            containerColor = MaterialTheme.colorScheme.surface
                        ) {
                            navItems.forEach { screen ->
                                NavigationRailItem(
                                    modifier = Modifier.padding(vertical = 16.dp),
                                    icon = { Icon(screen.icon, contentDescription = screen.title) },
                                    label = { Text(screen.title) },
                                    // Material components automatically handle selected/unselected text and icon colors.
                                    selected = currentRoute == screen.route,
                                    onClick = {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }

                        AppNavHost(
                            navController = navController,
                            viewModel = viewModel,
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun AppNavHost(
        navController: NavHostController,
        viewModel: MainViewModel,
        modifier: Modifier = Modifier
    ) {
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.SongList.route,
            modifier = modifier
        ) {
            composable(BottomNavItem.SongList.route) {
                // Pass the viewModels down to the screens
                SongListScreen(viewModel = viewModel) { song ->
                    viewModel.playSong(song)
                    navController.navigate(AppDestinations.NOW_PLAYING_ROUTE)
                }
            }
            composable(AppDestinations.SPEEDOMETER_ROUTE) {
                SpeedometerScreen(viewModel = vehicleStatusViewModel, mainViewModel = viewModel)
            }

            composable(AppDestinations.SETTINGS_ROUTE) {
                // In a real app, you would likely pass the viewModel here too.
                SettingsScreen(viewModel = viewModel)
            }

            composable(AppDestinations.NOW_PLAYING_ROUTE) {
                NowPlayingScreen(
                    viewModel = viewModel
                ) {
                    navController.popBackStack()
                }
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                viewModel.loadData()
            } else {
                // Handle the case where the user denies the permission.
            }
        }

    fun askForPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_AUDIO)
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }
}
