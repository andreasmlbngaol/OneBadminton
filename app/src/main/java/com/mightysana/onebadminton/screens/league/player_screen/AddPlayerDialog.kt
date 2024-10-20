package com.mightysana.onebadminton.screens.league.player_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mightysana.onebadminton.R
import com.mightysana.onebadminton.screens.league.LeagueViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlayerDialog(
    onDismiss: () -> Unit,
    viewModel: LeagueViewModel,
    onSave: (String, String) -> Unit
) {
    val playerName by viewModel.name.collectAsState()
    val playerInitial by viewModel.initial.collectAsState()

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
        }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
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
                    keyboardActions = KeyboardActions(onDone = { onSave(playerName, playerInitial) })
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
                        onClick = { onSave(playerName, playerInitial) },
                        colors = ButtonDefaults.buttonColors().copy(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) { Text(stringResource(R.string.save), color = MaterialTheme.colorScheme.onPrimary) }
                }
            }
        }
    }
}