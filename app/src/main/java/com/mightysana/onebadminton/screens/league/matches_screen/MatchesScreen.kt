package com.mightysana.onebadminton.screens.league.matches_screen

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.mightysana.onebadminton.composable.FinishMatchDialog
import com.mightysana.onebadminton.composable.MatchCard
import com.mightysana.onebadminton.screens.league.LeagueViewModel
import com.mightysana.onebadminton.screens.league.MatchFormValidationResult
import com.mightysana.onebadminton.screens.league.MatchScoreValidationResult
import com.mightysana.onebadminton.screens.league.SCHEDULED
import com.mightysana.onebadminton.screens.league.STARTED
import com.mightysana.onebadminton.toastMessage

@Composable
fun MatchesScreen(
    viewModel: LeagueViewModel,
    onNavigateToRandom: () -> Unit
) {
    val league by viewModel.league.collectAsState()
    val matches = league.matches.filterNotNull().sortedBy {
        when(it.status) {
            STARTED -> 1
            SCHEDULED -> 2
            else -> 3
        }
    }
    val isAddMatchDialogVisible by viewModel.isAddMatchDialogVisible.collectAsState()
    val isFinishMatchDialogVisible by viewModel.isFinishMatchDialogVisible.collectAsState()
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
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(matches) { match ->
                val matchId = match.id
                MatchCard(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    viewModel = viewModel,
                    match = match,
                    onButtonClick = {
                        if(match.status == SCHEDULED) {
                            viewModel.startMatch(matchId)
                        } else {
                            viewModel.setFinishedMatchId(matchId)
                            when (viewModel.validateMatchScore()) {
                                MatchScoreValidationResult.Valid -> {
                                    Log.d("MatchesScreen", "Valid")
                                    viewModel.showFinishMatchDialog()
                                }
                                MatchScoreValidationResult.Invalid -> {
                                    Log.d("MatchesScreen", "Invalid")
                                    context.toastMessage(R.string.invalid_score)
                                }
                            }
                        }
                    }
                )
            }
        }
        AnimatedVisibility(isFinishMatchDialogVisible) {
            val matchId by viewModel.finishedMatchId.collectAsState()
            FinishMatchDialog(
                onDismiss = { viewModel.dismissFinishMatchDialog() },
                viewModel = viewModel,
                onSave = { viewModel.finishMatch(matchId) }
            )
        }
    }

    AnimatedVisibility(isAddMatchDialogVisible) {
        AddMatchDialog(
            onDismiss = { viewModel.dismissAddMatchDialog() },
            viewModel = viewModel,
            onSave = {
                when (viewModel.validateMatchForm()) {
                    MatchFormValidationResult.Valid -> {
                        viewModel.addMatch()
                        Log.d("MatchesScreen", "MatchesScreen: ${viewModel.league.value.matches}")
                        context.toastMessage(R.string.match_added)
                    }

                    MatchFormValidationResult.PlayerIsBlank -> {
                        context.toastMessage(R.string.player_is_blank)
                    }

                    MatchFormValidationResult.PlayerDuplicate -> {
                        context.toastMessage(R.string.player_duplicate)
                    }
                }
            }
        )
    }
}
