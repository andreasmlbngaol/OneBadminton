package com.mightysana.onebadminton.screens.league

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mightysana.onebadminton.isOdd
import com.mightysana.onebadminton.model.LeagueRepository
import com.mightysana.onebadminton.properties.Doubles
import com.mightysana.onebadminton.properties.League
import com.mightysana.onebadminton.properties.Match
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

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab

    private val _showAddPlayerDialog = MutableStateFlow(false)
    val showAddPlayerDialog: StateFlow<Boolean> = _showAddPlayerDialog

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _initial = MutableStateFlow("")
    val initial: StateFlow<String> = _initial

    fun setName(name: String) {
        _name.value = name
    }

    fun setInitial(initial: String) {
        _initial.value = initial.uppercase()
    }

    fun resetName() {
        setName("")
    }

    fun resetInitial() {
        setInitial("")
    }

    private fun resetNameAndInitial() {
        resetName()
        resetInitial()
    }

    fun isNameLengthValid(): Boolean {
        return _name.value.length <= 20
    }

    fun isInitialLengthValid(): Boolean {
        return _initial.value.length <= 3
    }

    fun isNameBlank(): Boolean {
        return _name.value.isBlank()
    }

    fun isInitialBlank(): Boolean {
        return _initial.value.isBlank()
    }

    fun validateForm(): FormValidationResult {
        return when {
            isNameBlank() -> FormValidationResult.NameIsBlank
            !isNameLengthValid() -> FormValidationResult.NameTooLong
            isInitialBlank() -> FormValidationResult.InitialIsBlank
            !isInitialLengthValid() -> FormValidationResult.InitialTooLong
            else -> FormValidationResult.Valid
        }
    }

    private fun setShowAddPlayerDialog(show: Boolean) {
        _showAddPlayerDialog.value = show
    }

    fun showAddPlayerDialog() {
        setShowAddPlayerDialog(true)
    }

    fun dismissAddPlayerDialog() {
        setShowAddPlayerDialog(false)
    }

    fun setSelectedTab(tab: Int) {
        _selectedTab.value = tab
    }

    suspend fun fetchLeague(id: Int) {
        _league.value = repository.getLeague(id)
        _league.value = _league.value.copy(players = _league.value.players.filterNotNull())
    }

    fun addPlayer(name: String, initial: String, leagueId: Int) {
        viewModelScope.launch {
            val lastPlayer = repository.getLastPlayer(leagueId)
            val newId = if(lastPlayer != null) lastPlayer.id + 1 else 1
            val newPlayer = Player(newId, name, initial)
            resetNameAndInitial()
            repository.addPlayer(newPlayer, leagueId)
            fetchLeague(leagueId)
        }
    }

    private fun randomizeDouble(players: List<Player>): List<Doubles> {
        val shuffledPlayers = players.shuffled().toMutableList()
        val playersCount = shuffledPlayers.size
        val doubles = mutableListOf<Doubles>()

        if(playersCount.isOdd()) {
            val playerToPlayTwice = shuffledPlayers.random()
            shuffledPlayers.add(playerToPlayTwice)
        }

        while (shuffledPlayers.isNotEmpty()) {
            val player1 = shuffledPlayers.removeFirst()
            val player2 = shuffledPlayers.removeFirst()
            doubles.add(Doubles(player1, player2))
        }
        return doubles
    }

    fun generateMatches(players: List<Player>) {
        viewModelScope.launch {
            val doubles = randomizeDouble(players)
            val shuffledDoubles = doubles.shuffled().toMutableList()
            val doublesCount = shuffledDoubles.size
            val matches = mutableListOf<Match>()

            if (doublesCount.isOdd()) {
                val enemies = shuffledDoubles.indices.associateWith { 0 }.toMutableMap()
                var i = 0
//                while(shuffledDoubles.isNotEmpty()) {
                while(i < doublesCount) {
                    val double1 = shuffledDoubles[i]
                    var double2: Doubles
                    var j = i
                    while(enemies[i]!! < 2) {
                        j++
                        if(enemies[j] == 0 || i == doublesCount - 2) {
                            double2 = shuffledDoubles[j]
                            if(enemies[i] == 0) {
                                matches.add(Match(double1, double2))
                            } else {
                                matches.add(Match(double2, double1))
                            }
                            enemies[i] = enemies[i]!! + 1
                            enemies[j] = enemies[j]!! + 1
                        }
                    }
                    Log.d("LeagueViewModel", "enemies: $enemies")
//                    shuffledDoubles.removeAt(i)
                    i++
                }
            } else {
                while (shuffledDoubles.isNotEmpty()) {
                    val doubles1 = shuffledDoubles.removeFirst()
                    val doubles2 = shuffledDoubles.removeFirst()
                    matches.add(Match(doubles1, doubles2))
                }
            }
            fetchLeague(_league.value.id)
            Log.d("LeagueViewModel", "Generated matches: $matches")
        }
    }
}

sealed class FormValidationResult {
    data object Valid : FormValidationResult()       // Jika form valid
    data object NameTooLong : FormValidationResult() // Jika name terlalu panjang
    data object NameIsBlank : FormValidationResult() // Jika name kosong
    data object InitialTooLong : FormValidationResult() // Jika initial terlalu panjang
    data object InitialIsBlank : FormValidationResult() // Jika initial kosong
}
