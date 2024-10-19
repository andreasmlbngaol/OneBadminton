package com.mightysana.onebadminton.screens.league

import android.util.Log
import android.widget.Toast
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mightysana.onebadminton.R
import com.mightysana.onebadminton.properties.Match
import com.mightysana.onebadminton.properties.Player
import com.mightysana.onebadminton.properties.Table
import com.mightysana.onebadminton.properties.TableColumn
import com.mightysana.onebadminton.properties.TableData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeagueScreen(
    navController: NavController,
    leagueId: Int,
    viewModel: LeagueViewModel = hiltViewModel()
) {
    LaunchedEffect(leagueId) {
        viewModel.fetchLeague(leagueId)
    }

    var showAddPlayerDialog by remember { mutableStateOf(false) }
    val league by viewModel.league.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    val contentController = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                            )
                    }
                },
                title = {
                    Text(text = league.name, style = MaterialTheme.typography.titleLarge)
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                navController = contentController
            )
        },
        floatingActionButton = {
            AnimatedVisibility(selectedTab == 2) {
                FloatingActionButton(
                    onClick = { showAddPlayerDialog = true }
                ) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_player))
                }
            }

        }
    ) { paddingValues ->
        NavHost(
            navController = contentController,
            startDestination = MATCHES,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(MATCHES) {
                MatchesScreen(
                    matches = league.matches
                ) {
                    selectedTab = 2
                    contentController.navigateAndPopUp(RANDOM_PAIR, MATCHES)
                }
            }
            composable(LEADERBOARD) {
                 LeaderboardScreen(players = league.players) {
                     selectedTab = 2
                     contentController.navigateAndPopUp(RANDOM_PAIR, LEADERBOARD)
                 }
            }
            composable(RANDOM_PAIR) {
                RandomPairScreen(
                    leagueId = leagueId,
                    players = league.players,
                    onRandomize = {
                    },
                    showDialog = showAddPlayerDialog,
                    onDismissDialog = { showAddPlayerDialog = false },
                    viewModel = viewModel
                ) {
                    showAddPlayerDialog = true
                }
            }
        }
    }
}

fun NavController.navigateAndPopUp(route: String, popUp: String) {
    this.navigate(route) {
        launchSingleTop = true
        popUpTo(popUp) { inclusive = true }
    }
}

@Composable
fun BottomNavigationBar(selectedTab: Int, onTabSelected: (Int) -> Unit, navController: NavHostController) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
            label = { Text(stringResource(R.string.matches_navbar)) },
            selected = selectedTab == 0,
            onClick = {
                val prevTab = TABS[selectedTab]
                onTabSelected(0)
                navController.navigateAndPopUp(MATCHES, prevTab)
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text(stringResource(R.string.leaderboard_navbar)) },
            selected = selectedTab == 1,
            onClick = {
                val prevTab = TABS[selectedTab]
                onTabSelected(1)
                navController.navigateAndPopUp(LEADERBOARD, prevTab)
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Refresh, contentDescription = null) },
            label = { Text(stringResource(R.string.random_pair_navbar)) },
            selected = selectedTab == 2,
            onClick = {
                val prevTab = TABS[selectedTab]
                onTabSelected(2)
                navController.navigateAndPopUp(RANDOM_PAIR, prevTab)
            }
        )
    }
}

@Composable
fun MatchesScreen(matches: List<Match>, onNavigateToRandom: () -> Unit) {
    if (matches.isEmpty()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 8.dp).fillMaxSize()
        ) {
            Text(text = stringResource(R.string.no_matches))
            TextButton(
                onClick = onNavigateToRandom
            ) {
                Text(text = stringResource(R.string.redirect_to_randomize))
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            items(matches) { match ->
                val player1 = match.couple1.player1
                val player2 = match.couple1.player2
                val player3 = match.couple2.player1
                val player4 = match.couple2.player2
                Text("$player1 & $player2 vs $player3 & $player4")
            }
        }
    }
}

@Composable
fun LeaderboardScreen(players: List<Player>, onNavigateToAddPlayer: () -> Unit) {
    if (players.isEmpty()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 8.dp).fillMaxSize()
        ) {
            Text(text = stringResource(R.string.no_players))
            TextButton(
                onClick = onNavigateToAddPlayer
            ) {
                Text(text = stringResource(R.string.redirect_to_add_player))
            }
        }
    } else {
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxSize()
        ) {
            Table(
                columnName = listOf(
                    TableColumn("Name", textAlign = TextAlign.Start),
                    TableColumn("", 0.4f),
                    TableColumn("M", 0.3f),
                    TableColumn("SM", 0.3f),
                    TableColumn("SK", 0.3f),
                    TableColumn("P", 0.3f),
                ),
                data = players.map {
                    listOf(
                        TableData(it.name, fontWeight = FontWeight.SemiBold),
                        TableData(
                            it.initial,
                            0.4f,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        ),
                        TableData(
                            (it.losses + it.wins).toString(),
                            0.3f,
                            textAlign = TextAlign.Center
                        ),
                        TableData(it.scoreIn.toString(), 0.3f, textAlign = TextAlign.Center),
                        TableData(it.scoreOut.toString(), 0.3f, textAlign = TextAlign.Center),
                        TableData(
                            it.points.toString(),
                            0.3f,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(0.99f)
            )
        }
    }
}

@Composable
fun RandomPairScreen(
    leagueId: Int,
    players: List<Player>,
    onRandomize: () -> Unit,
    showDialog: Boolean,
    onDismissDialog: () -> Unit,
    viewModel: LeagueViewModel,
    onAddPlayer: () -> Unit
) {
    val context = LocalContext.current
    var isDialogVisible by remember { mutableStateOf(showDialog) } // Variabel yang mengontrol dialog

    LaunchedEffect(showDialog) {
        isDialogVisible = showDialog
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (players.isEmpty()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 8.dp).fillMaxSize()
            ) {
                Text(text = stringResource(R.string.no_players))
                TextButton(
                    onClick = onAddPlayer
                ) {
                    Text(text = stringResource(R.string.press_add_player))
                }
            }
        } else {
            Button(onClick = onRandomize) {
                Text("Acak Pasangan")
            }
        }
    }
    AnimatedVisibility(isDialogVisible) {
        AddPlayerDialog(
            onDismiss = onDismissDialog,
            onSave = { name, initial ->
                if(name.isNotBlank() && initial.isNotBlank()) {
                    viewModel.addPlayer(name, initial, leagueId)
                    isDialogVisible = false
                } else if(name.isBlank()) {
                    Toast.makeText(context, "Player name cannot be empty", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Player initial cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlayerDialog(
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var playerName by remember { mutableStateOf("") }
    var playerInitial by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column {
                OutlinedTextField(
                    value = playerName,
                    onValueChange = { playerName = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nama Pemain") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = playerInitial,
                    onValueChange = { playerInitial = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Inisial") },
                    singleLine = true
                )
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors().copy(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Cancel", color = MaterialTheme.colorScheme.onError)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    TextButton(
                        onClick = { onSave(playerName, playerInitial) },
                        colors = ButtonDefaults.buttonColors().copy(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Save", color = MaterialTheme.colorScheme.onPrimary)
                    }

                }
            }
        }
    }
//    AlertDialog(
//        onDismissRequest = onDismiss,
//        title = {
//            Text("Tambah Pemain")
//        },
//        text = {
//            Column {
//                OutlinedTextField(
//                    value = playerName,
//                    onValueChange = { playerName = it },
//                    modifier = Modifier.fillMaxWidth(),
//                    label = { Text("Nama Pemain") },
//                    singleLine = true
//                )
//                OutlinedTextField(
//                    value = playerInitial,
//                    onValueChange = { playerInitial = it },
//                    modifier = Modifier.fillMaxWidth(),
//                    label = { Text("Inisial") },
//                    singleLine = true
//                )
//            }
//        },
//        confirmButton = {
//            TextButton(
//                onClick = { onSave(playerName, playerInitial) },
//                colors = ButtonDefaults.buttonColors().copy(
//                    containerColor = MaterialTheme.colorScheme.primary
//                )
//            ) {
//                Text("Save", color = MaterialTheme.colorScheme.onPrimary)
//            }
//        },
//        dismissButton = {
//            TextButton(
//                onClick = onDismiss,
//                colors = ButtonDefaults.buttonColors().copy(
//                    containerColor = MaterialTheme.colorScheme.error
//                )
//            ) {
//                Text("Cancel", color = MaterialTheme.colorScheme.onError)
//            }
//        }
//    )
}

val MATCHES = "matches"
val LEADERBOARD = "leaderboard"
val RANDOM_PAIR = "random_pair"

val TABS = listOf(
    MATCHES,
    LEADERBOARD,
    RANDOM_PAIR
)