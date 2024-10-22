package com.mightysana.onebadminton.screens.league.player_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mightysana.onebadminton.R
import com.mightysana.onebadminton.composable.AddPlayerDialog
import com.mightysana.onebadminton.screens.league.PlayerFormValidationResult
import com.mightysana.onebadminton.screens.league.LeagueViewModel
import com.mightysana.onebadminton.toastMessage

@Composable
fun PlayerScreen(
    viewModel: LeagueViewModel,
    onRandomize: () -> Unit,
    onDismissDialog: () -> Unit,
    onAddPlayer: () -> Unit
) {
    val context = LocalContext.current
    val league by viewModel.league.collectAsState()
    val isDialogVisible by viewModel.showAddPlayerDialog.collectAsState()
    val leagueId = league.id
    val players = league.players.sortedBy { it.name }
    val isPlayerEmpty = players.isEmpty()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = if(isPlayerEmpty) Arrangement.Center else Arrangement.Top,
        modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp)
    ) {
        if (isPlayerEmpty) {
            Text(text = stringResource(R.string.no_players))
            TextButton(onClick = onAddPlayer) {
                Text(
                    text = stringResource(R.string.press_add_player),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Button(
                onClick = onRandomize,
                enabled = players.size >= 4
            ) { Text(stringResource(R.string.generate_double)) }
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement =  Arrangement.Top,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            ) {
                items(players) {
                    Text(text = "${it.name} (${it.initial})")
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    AnimatedVisibility(isDialogVisible) {
        AddPlayerDialog(
            onDismiss = onDismissDialog,
            viewModel = viewModel
        ) {
            when (viewModel.validatePlayerForm()) {
                PlayerFormValidationResult.Valid -> {
                    viewModel.addPlayer()
                    context.toastMessage(R.string.player_added)
                }
                PlayerFormValidationResult.NameIsBlank -> { context.toastMessage(R.string.player_name_empty) }
                PlayerFormValidationResult.NameTooLong -> { context.toastMessage(R.string.player_name_too_long) }
                PlayerFormValidationResult.InitialIsBlank -> { context.toastMessage(R.string.player_initial_empty) }
                PlayerFormValidationResult.InitialTooLong -> { context.toastMessage(R.string.player_initial_too_long) }
            }
        }
    }
}
