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
import com.mightysana.onebadminton.properties.League
import com.mightysana.onebadminton.screens.league.FormValidationResult
import com.mightysana.onebadminton.screens.league.LeagueViewModel
import com.mightysana.onebadminton.screens.league.toastMessage

@Composable
fun PlayerScreen(
    league: League,
    onRandomize: () -> Unit,
    onDismissDialog: () -> Unit,
    viewModel: LeagueViewModel,
    onAddPlayer: () -> Unit
) {
    val context = LocalContext.current
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
            TextButton(
                onClick = onAddPlayer
            ) {
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
        ) { name, initial ->
            when (viewModel.validateForm()) {
                FormValidationResult.Valid -> {
                    viewModel.addPlayer(name, initial, leagueId)
                    context.toastMessage(R.string.player_added)
                }
                FormValidationResult.NameIsBlank -> { context.toastMessage(R.string.player_name_empty) }
                FormValidationResult.NameTooLong -> { context.toastMessage(R.string.player_name_too_long) }
                FormValidationResult.InitialIsBlank -> { context.toastMessage(R.string.player_initial_empty) }
                FormValidationResult.InitialTooLong -> { context.toastMessage(R.string.player_initial_too_long) }
            }
        }
    }
}