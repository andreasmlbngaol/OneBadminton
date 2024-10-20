package com.mightysana.onebadminton

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

// ------------ MODAL BOTTOM SHEET ------------
//    ModalBottomSheet(
//        onDismissRequest = onDismiss,
//    ) {
//        Surface(
//            modifier = Modifier.fillMaxWidth().padding(16.dp)
//        ) {
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
//                Row(
//                    horizontalArrangement = Arrangement.End,
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    TextButton(
//                        onClick = onDismiss,
//                        colors = ButtonDefaults.buttonColors().copy(
//                            containerColor = MaterialTheme.colorScheme.error
//                        )
//                    ) {
//                        Text("Cancel", color = MaterialTheme.colorScheme.onError)
//                    }
//
//                    Spacer(modifier = Modifier.width(8.dp))
//
//                    TextButton(
//                        onClick = { onSave(playerName, playerInitial) },
//                        colors = ButtonDefaults.buttonColors().copy(
//                            containerColor = MaterialTheme.colorScheme.primary
//                        )
//                    ) {
//                        Text("Save", color = MaterialTheme.colorScheme.onPrimary)
//                    }
//
//                }
//            }
//        }
//    }


// AlertDialog
//AlertDialog(
//onDismissRequest = onDismiss,
//title = {
//    Text(stringResource(R.string.add_player))
//},
//text = {
//    Column {
//        OutlinedTextField(
//            value = playerName,
//            onValueChange = { viewModel.setName(it) },
//            modifier = Modifier.fillMaxWidth(),
//            label = { Text(stringResource(R.string.player_name_label)) },
//            singleLine = true
//        )
//        OutlinedTextField(
//            value = playerInitial,
//            onValueChange = { viewModel.setInitial(it) },
//            modifier = Modifier.fillMaxWidth(),
//            label = { Text(stringResource(R.string.player_initial_label)) },
//            singleLine = true
//        )
//    }
//},
//confirmButton = {
//    TextButton(
//        onClick = { onSave(playerName, playerInitial) },
//        colors = ButtonDefaults.buttonColors().copy(
//            containerColor = MaterialTheme.colorScheme.primary
//        )
//    ) {
//        Text("Save", color = MaterialTheme.colorScheme.onPrimary)
//    }
//},
//dismissButton = {
//    TextButton(
//        onClick = onDismiss,
//        colors = ButtonDefaults.buttonColors().copy(
//            containerColor = MaterialTheme.colorScheme.error
//        )
//    ) {
//        Text("Cancel", color = MaterialTheme.colorScheme.onError)
//    }
//}
//)