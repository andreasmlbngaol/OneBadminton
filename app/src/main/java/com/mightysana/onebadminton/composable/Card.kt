package com.mightysana.onebadminton.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mightysana.onebadminton.R
import com.mightysana.onebadminton.properties.League
import com.mightysana.onebadminton.properties.Match
import com.mightysana.onebadminton.screens.league.FINISHED
import com.mightysana.onebadminton.screens.league.LeagueViewModel
import com.mightysana.onebadminton.screens.league.SCHEDULED
import com.mightysana.onebadminton.screens.league.STARTED
import com.mightysana.onebadminton.toDisplayableTime

@Composable
fun LeagueCard(
    leaguePair:  Pair<Int, League>,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxWidth(0.9f)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = leaguePair.second.name,
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "id: ${leaguePair.first}",
                modifier = Modifier.padding(bottom = 8.dp),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun MatchCard(
    modifier: Modifier = Modifier,
    viewModel: LeagueViewModel,
    match: Match,
    onButtonClick: () -> Unit
) {
    val containerColor = if(match.status == SCHEDULED) MaterialTheme.colorScheme.surfaceVariant else (if(match.status == STARTED) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary)
    val contetColor = if(match.status == SCHEDULED) MaterialTheme.colorScheme.onSurfaceVariant else (if(match.status == STARTED) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onSecondary)
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors().copy(
            containerColor = containerColor,
            contentColor = contetColor,
        )
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            MatchCardTop(match = match)
            HorizontalDivider(Modifier.padding(vertical = 8.dp), color = contetColor)
            MatchCardContent(match = match, viewModel = viewModel)
            MatchCardBottom(
                Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp), match.status,
                onButtonClick
            )
        }
    }
}


@Composable
fun MatchCardTop(
    modifier: Modifier = Modifier.padding(horizontal = 16.dp),
    match: Match
) {
    Box(
        modifier.fillMaxWidth()
    ) {
        Text(
            modifier = modifier.fillMaxWidth(),
        text = "${LocalContext.current.getString(R.string.match)} ${match.id + 1}",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun MatchCardContent(
    modifier: Modifier = Modifier.padding(horizontal = 16.dp),
    match: Match,
    viewModel: LeagueViewModel
) {
    val status = match.status
    val duration = match.durationInMillis
    val doubles1 = match.doubles1
    val player1 = viewModel.getPlayer(doubles1.player1Id)
    val player2 = viewModel.getPlayer(doubles1.player2Id)
    val doubles2 = match.doubles2
    val player3 = viewModel.getPlayer(doubles2.player1Id)
    val player4 = viewModel.getPlayer(doubles2.player2Id)
    val score1 = match.score1
    val score2 = match.score2

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(status == FINISHED) {
            Text(
                text = "Duration: ${duration.toDisplayableTime()}",
                style = MaterialTheme.typography.titleMedium,
            )
        }
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(0.4f),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = player1.initial,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = player2.initial,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                ScoreBoard(
                    score = score1,
                    onScoreUp = { viewModel.addScore1(match.id) },
                    onScoreDown = { viewModel.removeScore1(match.id) },
                    matchStatus = status
                )
            }

            Text(
                modifier = Modifier.weight(0.2f),
                text = "VS",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )


            Row(
                modifier = Modifier.weight(0.4f),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ScoreBoard(
                    score = score2,
                    onScoreUp = { viewModel.addScore2(match.id) },
                    onScoreDown = { viewModel.removeScore2(match.id) },
                    matchStatus = status
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = player3.initial,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = player4.initial,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ScoreBoard(
    modifier: Modifier = Modifier,
    onScoreUp: () -> Unit,
    onScoreDown: () -> Unit,
    score: Int,
    matchStatus: String
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if(matchStatus == STARTED) {
            IconButton(
                onClick = onScoreUp
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = null
                )
            }
        }

        Text(
            text = score.toString(),
            style = MaterialTheme.typography.headlineSmall
        )

        if(matchStatus == STARTED) {
            IconButton(
                onClick = onScoreDown,
                enabled = score > 0
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun MatchCardBottom(
    modifier: Modifier = Modifier.padding(horizontal = 16.dp),
    matchStatus: String,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = modifier.fillMaxWidth()
    ) {
        val buttonContainerColor = if(matchStatus == SCHEDULED) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.errorContainer
        val buttonContentColor = if(matchStatus == SCHEDULED) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onErrorContainer
        Button(
            onClick = onClick,
            enabled = matchStatus != FINISHED,
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors().copy(
                containerColor = buttonContainerColor,
                contentColor = buttonContentColor,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Text(
                text = if(matchStatus == SCHEDULED) context.getString(R.string.start) else (if(matchStatus == STARTED) context.getString(R.string.finish) else context.getString(R.string.finished)),
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}