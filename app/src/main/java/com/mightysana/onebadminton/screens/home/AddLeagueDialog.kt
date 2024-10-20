package com.mightysana.onebadminton.screens.home

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mightysana.onebadminton.R

@Composable
fun AddLeagueDialog(
    onDismiss: () -> Unit,
    viewModel: HomeViewModel,
    onSave: (String) -> Unit
) {
    val leagueName by viewModel.leagueName.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_league)) },
        text = {
            OutlinedTextField(
                value = leagueName,
                onValueChange = { viewModel.setLeagueName(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.league_name_label)) },
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = { onSave(leagueName) })
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(leagueName) },
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