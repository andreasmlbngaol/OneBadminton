package com.mightysana.onebadminton.screens.home

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mightysana.onebadminton.R
import com.mightysana.onebadminton.composable.TextTopBar

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val leagues by viewModel.leagues.collectAsState()
    val isDialogVisible by viewModel.isDialogVisible.collectAsState()

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
                    Card(
                        colors = CardDefaults.cardColors().copy(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .clickable { navController.navigate("league/${it.first}") }
                            .fillMaxWidth(0.9f)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = it.second.name,
                                modifier = Modifier.padding(top = 8.dp),
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = "id: ${it.first}",
                                modifier = Modifier.padding(bottom = 8.dp),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
        AnimatedVisibility(isDialogVisible) {
            val errorMessage = stringResource(R.string.league_name_empty)
            AddLeagueDialog(
                onDismiss = { viewModel.dismissDialog() },
                viewModel = viewModel,
                onSave = { leagueName ->
                    if(leagueName.isNotBlank()) {
                        viewModel.addLeague(leagueName)
                        viewModel.dismissDialog()
                    } else { Toast.makeText(navController.context, errorMessage, Toast.LENGTH_SHORT).show() }
                }
            )
        }
    }
}