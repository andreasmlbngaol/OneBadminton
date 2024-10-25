package com.mightysana.onebadminton.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mightysana.onebadminton.R
import com.mightysana.onebadminton.screens.home.HomeViewModel
import com.mightysana.onebadminton.screens.league.LeagueViewModel

@Composable
fun AddLeagueDialog(
    onDismiss: () -> Unit,
    viewModel: HomeViewModel,
    onSave: () -> Unit
) {
    val leagueName by viewModel.leagueName.collectAsState()
    val gamePoint by viewModel.gamePoint.collectAsState()
    val isDeuceEnabled by viewModel.isDeuceEnabled.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_league)) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    shape = MaterialTheme.shapes.medium,
                    value = leagueName,
                    onValueChange = { viewModel.setLeagueName(it) },
                    label = { Text(stringResource(R.string.league_name_label)) },
                    singleLine = true,
                )
                OutlinedTextField(
                    shape = MaterialTheme.shapes.medium,
                    value = if(gamePoint == 0) "" else gamePoint.toString(),
                    onValueChange = { viewModel.setGamePoint(it.toIntOrNull() ?: 0) },
                    label = { Text(stringResource(R.string.game_point_label)) },
                    placeholder = { Text("21")},
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.isDeuceEnabled),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Switch(
                        checked = isDeuceEnabled,
                        onCheckedChange = { viewModel.setDeuceEnabled(it) },
                        colors = SwitchDefaults.colors().copy(
                            uncheckedThumbColor = MaterialTheme.colorScheme.errorContainer,
                            uncheckedTrackColor = MaterialTheme.colorScheme.error,
                            checkedThumbColor = MaterialTheme.colorScheme.primaryContainer,
                            checkedTrackColor = MaterialTheme.colorScheme.primary
                        ),
                        thumbContent = {
                            Text(
                                style = MaterialTheme.typography.bodySmall,
                                text = LocalContext.current.getString(if(isDeuceEnabled) R.string.on else R.string.off),
                                color = if(!isDeuceEnabled) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                        }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave() },
                colors = ButtonDefaults.buttonColors().copy(containerColor = MaterialTheme.colorScheme.primary)
            ) { Text(stringResource(R.string.save), color = MaterialTheme.colorScheme.onPrimary) }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors().copy(containerColor = MaterialTheme.colorScheme.error)
            ) { Text(stringResource(R.string.cancel), color = MaterialTheme.colorScheme.onError) }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomBottomSheet(
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = {
            IconButton(onClick = onDismiss) {
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        },
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) { content() }
    }
}

@Composable
fun AddPlayerDialog(
    viewModel: LeagueViewModel,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    val playerName by viewModel.name.collectAsState()
    val playerInitial by viewModel.initial.collectAsState()

    CustomBottomSheet(
        onDismiss = onDismiss
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.add_player),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                shape = MaterialTheme.shapes.medium,
                value = playerName,
                onValueChange = { viewModel.setName(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.player_name_label)) },
                singleLine = true,
                isError = !viewModel.isNameLengthValid(),
                supportingText = {
                    AnimatedVisibility(!viewModel.isNameLengthValid()) {
                        Text(stringResource(R.string.player_name_too_long))
                    }
                },
                trailingIcon = {
                    IconButton(
                        onClick = { viewModel.resetName() },
                        enabled = !viewModel.isNameBlank()
                    ) { Icon(Icons.Default.Clear, contentDescription = null) }
                }
            )
            OutlinedTextField(
                shape = MaterialTheme.shapes.medium,
                value = playerInitial,
                onValueChange = { viewModel.setInitial(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.player_initial_label)) },
                singleLine = true,
                isError = !viewModel.isInitialLengthValid(),
                supportingText = {
                    AnimatedVisibility(!viewModel.isInitialLengthValid()) {
                        Text(stringResource(R.string.player_initial_too_long))
                    }
                },
                trailingIcon = {
                    IconButton(
                        onClick = { viewModel.resetInitial() },
                        enabled = !viewModel.isInitialBlank()
                    ) { Icon(Icons.Default.Clear, contentDescription = null) }
                },
                keyboardActions = KeyboardActions(onDone = { onSave() })
            )
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) { Text(stringResource(R.string.cancel), color = MaterialTheme.colorScheme.onError) }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(
                    onClick = { onSave() },
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) { Text(stringResource(R.string.save), color = MaterialTheme.colorScheme.onPrimary) }
            }
        }
    }
}

@Composable
fun AddMatchDialog(
    viewModel: LeagueViewModel,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    val league by viewModel.league.collectAsState()
    val players = league.players

    val player1 by viewModel.player1.collectAsState()
    val player2 by viewModel.player2.collectAsState()
    val player3 by viewModel.player3.collectAsState()
    val player4 by viewModel.player4.collectAsState()

    var expanded1 by remember { mutableStateOf(false) }
    var expanded2 by remember { mutableStateOf(false) }
    var expanded3 by remember { mutableStateOf(false) }
    var expanded4 by remember { mutableStateOf(false) }

    val options = players.map { it.name }

    CustomBottomSheet(
        onDismiss = onDismiss
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OneDropdownMenu(
                    modifier = Modifier.weight(0.4f),
                    selected = if(player1 != null) player1!!.name else stringResource(R.string.select_player, 1),
                    options = options,
                    expanded = expanded1,
                    onExpandRequest = { expanded1 = true },
                    onDismiss = { expanded1 = false },
                ) { selected -> viewModel.setPlayer1(players.find { it.name == selected }!!) }
                Text(
                    stringResource(R.string.and_symbol),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(0.1f),
                    textAlign = TextAlign.Center
                )
                OneDropdownMenu(
                    modifier = Modifier.weight(0.4f),
                    selected = if(player2 != null) player2!!.name else stringResource(R.string.select_player, 2),
                    options = options,
                    expanded = expanded2,
                    onDismiss = { expanded2 = false },
                    onExpandRequest = { expanded2 = true }
                ) { selected -> viewModel.setPlayer2(players.find { it.name == selected }!!) }
            }
            Text("VS", style = MaterialTheme.typography.titleLarge)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OneDropdownMenu(
                    modifier = Modifier.weight(0.4f),
                    selected = if(player3 != null) player3!!.name else stringResource(R.string.select_player, 3),
                    options = options,
                    expanded = expanded3,
                    onExpandRequest = { expanded3 = true },
                    onDismiss = { expanded3 = false },
                ) { selected -> viewModel.setPlayer3(players.find { it.name == selected }!!) }
                Text(
                    stringResource(R.string.and_symbol),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(0.1f),
                    textAlign = TextAlign.Center
                )
                OneDropdownMenu(
                    modifier = Modifier.weight(0.4f),
                    selected = if(player4 != null) player4!!.name else stringResource(R.string.select_player, 4),
                    options = options,
                    expanded = expanded4,
                    onDismiss = { expanded4 = false },
                    onExpandRequest = { expanded4 = true }
                ) { selected -> viewModel.setPlayer4(players.find { it.name == selected }!!) }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) { Text(stringResource(R.string.cancel), color = MaterialTheme.colorScheme.onError) }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(
                    onClick = { onSave() },
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) { Text(stringResource(R.string.save), color = MaterialTheme.colorScheme.onPrimary) }
            }

        }
    }
}

@Composable
fun FinishMatchDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    viewModel: LeagueViewModel,
    onSave: () -> Unit
) {
    val league by viewModel.league.collectAsState()
    val matchId by viewModel.finishedMatchId.collectAsState()
    val match = league.matches[matchId!!]

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.confirmation)) },
        text = { Text(
            stringResource(R.string.finish_match_message, matchId?.plus(1).toString())
        )
        },
        confirmButton = {
            TextButton(
                onClick = onSave,
                colors = ButtonDefaults.buttonColors().copy(containerColor = MaterialTheme.colorScheme.primary)
            ) { Text(stringResource(R.string.save), color = MaterialTheme.colorScheme.onPrimary) }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors().copy(containerColor = MaterialTheme.colorScheme.error)
            ) { Text(stringResource(R.string.cancel), color = MaterialTheme.colorScheme.onError) }
        }
    )
}