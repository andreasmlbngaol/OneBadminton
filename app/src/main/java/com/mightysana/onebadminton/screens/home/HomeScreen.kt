package com.mightysana.onebadminton.screens.home

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mightysana.onebadminton.R
import com.mightysana.onebadminton.composable.AddLeagueDialog
import com.mightysana.onebadminton.composable.LeagueCard
import com.mightysana.onebadminton.composable.TextTopBar
import com.mightysana.onebadminton.isNaturalNumber
import com.mightysana.onebadminton.toastMessage

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val leagues by viewModel.leagues.collectAsState()
    val isAddMatchDialogVisible by viewModel.isAddMatchDialogVisible.collectAsState()

    Scaffold(
        topBar = {
            TextTopBar(
                text = stringResource(R.string.app_name),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.fillMaxWidth()
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showDialog() }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = if(leagues.isEmpty()) Modifier.padding(padding).fillMaxSize() else Modifier.padding(padding).fillMaxWidth(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if(leagues.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.no_leagues),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            } else {
                items(leagues.toList().reversed()) {
                    LeagueCard(leaguePair = it,) { navController.navigate("league/${it.first}") }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
        AnimatedVisibility(isAddMatchDialogVisible) {
            val context = navController.context
            AddLeagueDialog(
                onDismiss = { viewModel.dismissDialog() },
                viewModel = viewModel,
                onSave = {
                    when(viewModel.validateLeagueForm()) {
                        LeagueFormValidationResult.Valid -> {
                            viewModel.addLeague()
                            context.toastMessage(R.string.league_added)
                        }
                        LeagueFormValidationResult.NameIsBlank -> { context.toastMessage(R.string.league_name_empty) }
                        LeagueFormValidationResult.NameTooLong -> { context.toastMessage(R.string.league_name_too_long)}
                        LeagueFormValidationResult.GamePointIsBlank -> { context.toastMessage(R.string.game_point_is_blank)}
                        LeagueFormValidationResult.GamePointTooLow -> { context.toastMessage(R.string.game_point_too_low)}
                    }
                }
            )
        }
    }
}