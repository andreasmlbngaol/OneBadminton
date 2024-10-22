package com.mightysana.onebadminton.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mightysana.onebadminton.R
import com.mightysana.onebadminton.properties.League
import com.mightysana.onebadminton.properties.Match
import com.mightysana.onebadminton.screens.home.HomeViewModel
import com.mightysana.onebadminton.screens.league.FINISHED
import com.mightysana.onebadminton.screens.league.LeagueViewModel
import com.mightysana.onebadminton.screens.league.SCHEDULED
import com.mightysana.onebadminton.screens.league.STARTED

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
    viewModel: LeagueViewModel,
    match: Match,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    Card(

    ) {
        Column {
//            MatchCardTop()
//            MatchCardContent()
            Row(horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onClick, enabled = match.status != FINISHED) {
                    Text(text = if(match.status == SCHEDULED) context.getString(R.string.start) else (if(match.status == STARTED) context.getString(R.string.finish) else context.getString(R.string.finished)))
                }
            }
        }
    }
}