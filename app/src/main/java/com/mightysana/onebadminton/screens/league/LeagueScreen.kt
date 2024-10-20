package com.mightysana.onebadminton.screens.league

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mightysana.onebadminton.R
import com.mightysana.onebadminton.composable.AddFloatingActionButton
import com.mightysana.onebadminton.composable.BackTextTopBar
import com.mightysana.onebadminton.composable.BottomNavBar
import com.mightysana.onebadminton.composable.BottomNavBarItem
import com.mightysana.onebadminton.navigateAndPopUp
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

    val matchesRoute = "matches"
    val leaderboardRoute = "leaderboard"
    val playerRoute = "player"

    Scaffold(
        topBar = {
            BackTextTopBar(
                navController = navController,
                text = league.name,
                style = MaterialTheme.typography.titleLarge
            )
        },
        bottomBar = {
            val tabs = listOf(
                BottomNavBarItem(
                    selectedIcon = Icons.Filled.DateRange,
                    unselectedIcon = Icons.Outlined.DateRange,
                    labelResId = R.string.matches,
                    route = matchesRoute
                ),
                BottomNavBarItem(
                    selectedIcon = Icons.AutoMirrored.Filled.List,
                    unselectedIcon = Icons.AutoMirrored.Outlined.List,
                    labelResId = R.string.leaderboard,
                    route = leaderboardRoute
                ),
                BottomNavBarItem(
                    selectedIcon = Icons.Filled.Person,
                    unselectedIcon = Icons.Outlined.Person,
                    labelResId = R.string.player,
                    route = playerRoute
                )
            )
            BottomNavBar(
                selectedTab = selectedTab,
                navController = contentController,
                tabs = tabs,
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
            startDestination = matchesRoute,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(matchesRoute) {
                MatchesScreen(league = league) {
                    viewModel.setSelectedTab(2)
                    contentController.navigateAndPopUp(playerRoute, matchesRoute)
                }
            }
            composable(leaderboardRoute) {
                 LeaderboardScreen(players = league.players) {
                     viewModel.setSelectedTab(2)
                     contentController.navigateAndPopUp(playerRoute, leaderboardRoute)
                 }
            }
            composable(playerRoute) {
                PlayerScreen(
                    league = league,
                    viewModel = viewModel,
                    onDismissDialog = { viewModel.dismissAddPlayerDialog() },
                    onAddPlayer = { viewModel.showAddPlayerDialog() },
                    onRandomize = {
                        viewModel.generateMatches(league.players)
                        viewModel.setSelectedTab(0)
                        contentController.navigateAndPopUp(matchesRoute, playerRoute)
                    }
                )
            }
        }
    }
}