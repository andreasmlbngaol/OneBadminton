package com.mightysana.onebadminton.screens.league

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mightysana.onebadminton.model.LeagueRepository
import com.mightysana.onebadminton.properties.League
import com.mightysana.onebadminton.properties.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeagueViewModel @Inject constructor(
    private val repository: LeagueRepository
): ViewModel() {
    private val _league = MutableStateFlow(League())
    val league: StateFlow<League> = _league

    suspend fun fetchLeague(id: Int) {
        _league.value = repository.getLeague(id)
        _league.value = _league.value.copy(players = _league.value.players.filterNotNull())
        Log.d("LeagueViewModel", "Fetched league: ${_league.value}")
    }

    fun addPlayer(name: String, initial: String, leagueId: Int) {
        viewModelScope.launch {
            val lastPlayer = repository.getLastPlayer(leagueId)
            val newId = if(lastPlayer != null) lastPlayer.id + 1 else 1
            val newPlayer = Player(newId, name, initial)
            Log.d("LeagueViewModel", "Adding player: $newPlayer")
            repository.addPlayer(newPlayer, leagueId)
            fetchLeague(leagueId)
        }
    }

}