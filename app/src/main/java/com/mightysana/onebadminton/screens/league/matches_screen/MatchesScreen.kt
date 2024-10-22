package com.mightysana.onebadminton.screens.league.matches_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mightysana.onebadminton.R
import com.mightysana.onebadminton.composable.AddMatchDialog
import com.mightysana.onebadminton.screens.league.LeagueViewModel
import com.mightysana.onebadminton.screens.league.MatchFormValidationResult
import com.mightysana.onebadminton.toastMessage

@Composable
fun MatchesScreen(
    viewModel: LeagueViewModel,
    onDismissDialog: () -> Unit,
    onNavigateToRandom: () -> Unit
) {
    val league by viewModel.league.collectAsState()
    val matches = league.matches
    val isDialogVisible by viewModel.showAddMatchDialog.collectAsState()
    val context = LocalContext.current
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
                val player1 = match.doubles1.player1
                val player2 = match.doubles1.player2
                val player3 = match.doubles2.player1
                val player4 = match.doubles2.player2
                Text("$player1 & $player2 vs $player3 & $player4")
            }
        }
    }

    AnimatedVisibility(isDialogVisible) {
        AddMatchDialog(
            onDismiss = onDismissDialog,
            viewModel = viewModel
        ) {
//            when (viewModel.validateMatchForm()) {
//                MatchFormValidationResult.Valid -> {
//                    viewModel.addPlayer()
//                    context.toastMessage(R.string.player_added)
//                }
//            }
            context.toastMessage(R.string.save)
        }
    }

}
