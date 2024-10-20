package com.mightysana.onebadminton.screens.league

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mightysana.onebadminton.R
import com.mightysana.onebadminton.composable.AddFloatingActionButton
import com.mightysana.onebadminton.composable.BackTextTopBar
import com.mightysana.onebadminton.composable.BottomNavBar
import com.mightysana.onebadminton.navigateAndPopUp
import com.mightysana.onebadminton.properties.League
import com.mightysana.onebadminton.properties.Match
import com.mightysana.onebadminton.properties.Player
import com.mightysana.onebadminton.properties.Table
import com.mightysana.onebadminton.properties.TableColumn
import com.mightysana.onebadminton.properties.TableData
import com.mightysana.onebadminton.screens.league.player_screen.PlayerScreen

@Composable
fun LeagueScreen(
    navController: NavController,
    leagueId: Int,
    viewModel: LeagueViewModel = hiltViewModel()
) {
    LaunchedEffect(leagueId) {
        viewModel.fetchLeague(leagueId)
    }

    val league by viewModel.league.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val contentController = rememberNavController()

    Scaffold(
        topBar = {
            BackTextTopBar(
                navController = navController,
                text = league.name,
                style = MaterialTheme.typography.titleLarge
            )
        },
        bottomBar = {
            BottomNavBar(
                selectedTab = selectedTab,
                navController = contentController,
            ) { viewModel.setSelectedTab(it) }
        },
        floatingActionButton = {
            AnimatedVisibility(selectedTab == 2) {
                AddFloatingActionButton { viewModel.showAddPlayerDialog() }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = contentController,
            startDestination = MATCHES,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(MATCHES) {
                MatchesScreen(league = league) {
                    viewModel.setSelectedTab(2)
                    contentController.navigateAndPopUp(PLAYER, MATCHES)
                }
            }
            composable(LEADERBOARD) {
                 LeaderboardScreen(players = league.players) {
                     viewModel.setSelectedTab(2)
                     contentController.navigateAndPopUp(PLAYER, LEADERBOARD)
                 }
            }
            composable(PLAYER) {
                PlayerScreen(
                    league = league,
                    viewModel = viewModel,
                    onDismissDialog = { viewModel.dismissAddPlayerDialog() },
                    onAddPlayer = { viewModel.showAddPlayerDialog() },
                    onRandomize = { }
                )
            }
        }
    }
}

const val MATCHES = "matches"
const val LEADERBOARD = "leaderboard"
const val PLAYER = "player"

val TABS = listOf(
    MATCHES,
    LEADERBOARD,
    PLAYER
)

fun Context.toastMessage(
    @StringRes message: Int,
    duration: Int = Toast.LENGTH_SHORT
) {
    Toast.makeText(
        this,
        this.getString(message),
        duration
    ).show()
}