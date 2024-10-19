package com.mightysana.onebadminton

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mightysana.onebadminton.screens.home.HomeScreen
import com.mightysana.onebadminton.screens.league.LeagueScreen
import com.mightysana.onebadminton.ui.theme.OneBadmintonTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            OneBadmintonTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = HOME,
                    ) {
                        composable(HOME) {
                            HomeScreen(navController)
                        }

                        composable("$LEAGUE/{leagueId}") {
                            val id = it.arguments?.getString("leagueId")?.toInt()
                            if (id != null) {
                                LeagueScreen(navController, id)
                            }
                        }
                    }
                }
            }
        }
    }
}