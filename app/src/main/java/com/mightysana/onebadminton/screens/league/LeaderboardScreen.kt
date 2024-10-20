package com.mightysana.onebadminton.screens.league

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.mightysana.onebadminton.R
import com.mightysana.onebadminton.properties.Player
import com.mightysana.onebadminton.properties.Table
import com.mightysana.onebadminton.properties.TableColumn
import com.mightysana.onebadminton.properties.TableData

@Composable
fun LeaderboardScreen(
    players: List<Player>,
    onNavigateToAddPlayer: () -> Unit
) {
    val isPlayerEmpty = players.isEmpty()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = if(isPlayerEmpty) Arrangement.Center else Arrangement.Top,
        modifier = Modifier.padding(horizontal = (if(isPlayerEmpty) 8 else 0).dp).fillMaxSize()
    ) {
        if (isPlayerEmpty) {
            Text(text = stringResource(R.string.no_players))
            TextButton(onClick = onNavigateToAddPlayer) { Text(text = stringResource(R.string.redirect_to_add_player)) }
        } else {
            val columns = listOf(
                TableColumn(stringResource(R.string.rank), 0.3f),
                TableColumn(stringResource(R.string.name), textAlign = TextAlign.Start),
                TableColumn(stringResource(R.string.match_count), 0.3f),
                TableColumn(stringResource(R.string.score_scored), 0.3f),
                TableColumn(stringResource(R.string.score_conceded), 0.3f),
                TableColumn(stringResource(R.string.points), 0.3f),
            )
            val sortedPlayers = players.sortedWith(
                compareByDescending<Player> { it.points }
                    .thenBy { it.matches }
                    .thenByDescending { it.wins }
                    .thenBy { it.losses }
                    .thenByDescending { it.scoreIn }
                    .thenBy { it.scoreOut }
                    .thenBy { it.name }
            )
            val data = sortedPlayers.mapIndexed { index, it ->
                listOf(
                    TableData(
                        content = (index + 1).toString(),
                        weight = 0.3f, TextAlign.Center
                    ),
                    TableData(
                        content = it.name,
                        fontWeight = FontWeight.SemiBold
                    ),
                    TableData(
                        content = (it.losses + it.wins).toString(),
                        weight = 0.3f,
                        textAlign = TextAlign.Center
                    ),
                    TableData(
                        content = it.scoreIn.toString(),
                        weight = 0.3f,
                        textAlign = TextAlign.Center
                    ),
                    TableData(
                        content = it.scoreOut.toString(),
                        weight = 0.3f,
                        textAlign = TextAlign.Center
                    ),
                    TableData(
                        content = it.points.toString(),
                        weight = 0.3f,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                )
            }

//            Text(
//                text = stringResource(R.string.leaderbord),
//                style = MaterialTheme.typography.titleLarge,
//                modifier = Modifier.padding(bottom = 8.dp),
//                textDecoration = TextDecoration.Underline
//            )

            Table(
                columnName = columns,
                data = data,
                modifier = Modifier.fillMaxWidth(0.95f).padding(bottom = 8.dp)
            )

        }
    }
}
