package com.example.composemediaplayer.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.core.content.ContextCompat
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.composemediaplayer.R
import com.example.composemediaplayer.ui.childview.BottomNavItem
import com.example.composemediaplayer.ui.screen.NowPlayingScreen
import com.example.composemediaplayer.ui.screen.SongListScreen
import com.example.composemediaplayer.ui.screen.speed.SpeedometerScreen
import com.example.composemediaplayer.ui.screen.speed.VehicleStatusViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    private val vehicleStatusViewModel: VehicleStatusViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        window.decorView.setBackgroundColor(
            ContextCompat.getColor(this, R.color.background_color)
        )
        askForPermission()
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                val bottomNavItems = listOf(
                    BottomNavItem.SongList,
                    BottomNavItem.Speedometer,
                )
                Scaffold(
                    containerColor = Color.Transparent,
                    bottomBar = {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentRoute = navBackStackEntry?.destination?.route
                        if (currentRoute in bottomNavItems.map { it.route }) {
                            NavigationBar(
                                containerColor = colorResource(id = R.color.bottom_bar_color)
                            ) {
                                bottomNavItems.forEach { screen ->
                                    NavigationBarItem(
                                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                                        label = { Text(screen.title) },
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
                        }
                    }
                ) { innerPadding ->
                    AppNavHost(
                        navController = navController,
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
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
                SongListScreen(viewModel = viewModel) { song ->
                    viewModel.playSong(song)
                    navController.navigate("nowPlaying")
                }
            }
            composable(BottomNavItem.Speedometer.route) {
                SpeedometerScreen(vehicleStatusViewModel)
            }

            // Other destinations
            composable("nowPlaying") {
                NowPlayingScreen(viewModel = viewModel) {
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
