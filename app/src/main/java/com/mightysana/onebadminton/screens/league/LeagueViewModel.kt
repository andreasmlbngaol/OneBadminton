package com.mightysana.onebadminton.screens.league

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mightysana.onebadminton.isNull
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

    private val _showAddMatchDialog = MutableStateFlow(false)
    val showAddMatchDialog: StateFlow<Boolean> = _showAddMatchDialog

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _initial = MutableStateFlow("")
    val initial: StateFlow<String> = _initial

    private val _player1 = MutableStateFlow<Player?>(null)
    val player1: StateFlow<Player?> = _player1

    private val _player2 = MutableStateFlow<Player?>(null)
    val player2: StateFlow<Player?> = _player2

    private val _player3 = MutableStateFlow<Player?>(null)
    val player3: StateFlow<Player?> = _player3

    private val _player4 = MutableStateFlow<Player?>(null)
    val player4: StateFlow<Player?> = _player4

    fun observeLeague(id: Int) {
        Log.d("LeagueViewModel", "Observing league with ID: $id")

        repository.observeLeague(id) { leagueData ->
            _league.value = leagueData
        }
    }

    fun setPlayer1(player: Player) {
        _player1.value = player
    }

    fun setPlayer2(player: Player) {
        _player2.value = player
    }

    fun setPlayer3(player: Player) {
        _player3.value = player
    }

    fun setPlayer4(player: Player) {
        _player4.value = player
    }

    fun resetPlayer() {
        _player1.value = null
        _player2.value = null
        _player3.value = null
        _player4.value = null
    }

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

    private fun isPlayerBlank(): Boolean {
        return _player1.value.isNull() ||
                _player2.value.isNull() ||
                _player3.value.isNull() ||
                _player4.value.isNull()
    }

    private fun isPlayerDuplicated(): Boolean {
        val players = listOf(
            _player1.value,
            _player2.value,
            _player3.value,
            _player4.value,
        )
        return players.any { player ->
            players.count { it == player } > 1
        }
    }

    fun validatePlayerForm(): PlayerFormValidationResult {
        return when {
            isNameBlank() -> PlayerFormValidationResult.NameIsBlank
            !isNameLengthValid() -> PlayerFormValidationResult.NameTooLong
            isInitialBlank() -> PlayerFormValidationResult.InitialIsBlank
            !isInitialLengthValid() -> PlayerFormValidationResult.InitialTooLong
            else -> PlayerFormValidationResult.Valid
        }
    }

    fun validateMatchForm(): MatchFormValidationResult {
        return when {
            isPlayerBlank() -> MatchFormValidationResult.PlayerIsBlank
            isPlayerDuplicated() -> MatchFormValidationResult.PlayerDuplicate
            else -> MatchFormValidationResult.Valid
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
        resetNameAndInitial()
    }

    private fun setShowAddMatchDialog(show: Boolean) {
        _showAddMatchDialog.value = show
    }

    fun showAddMatchDialog() {
        setShowAddMatchDialog(true)
    }

    fun dismissAddMatchDialog() {
        setShowAddMatchDialog(false)
        resetPlayer()
    }

    fun setSelectedTab(tab: Int) {
        if (_selectedTab.value != tab) {
            _selectedTab.value = tab
        }
    }

    fun addPlayer() {
        viewModelScope.launch {
            val name = _name.value
            val initial = _initial.value

            val leagueId = _league.value.id
            val lastPlayer = repository.getLastPlayer(leagueId)
            val newId = if(lastPlayer != null) lastPlayer.id + 1 else 0
            val newPlayer = Player(newId, name, initial)
            resetNameAndInitial()
            repository.addPlayer(newPlayer, leagueId)
        }
    }

    fun addMatch() {
        viewModelScope.launch {
            val doubles1 = Doubles(_player1.value!!, _player2.value!!)
            val doubles2 = Doubles(_player3.value!!, _player4.value!!)

            val leagueId = _league.value.id

            val lastMatch = repository.getLastMatch(leagueId)
            val newId = if(lastMatch != null) lastMatch.id + 1 else 1
            val newMatch = Match(newId, doubles1, doubles2)
            resetPlayer()
            repository.addMatch(newMatch, leagueId)
            dismissAddMatchDialog()
        }
    }

    private fun randomizeDouble(players: List<Player>): List<Doubles> {
        val shuffledPlayers = players.shuffled().toMutableList()
        val playersCount = shuffledPlayers.size
        val doubles = mutableListOf<Doubles>()

        if(playersCount.isOdd()) {
            val playerToPlayTwice = shuffledPlayers.first()
            shuffledPlayers.add(playerToPlayTwice)
        }

        while (shuffledPlayers.isNotEmpty()) {
            val player1 = shuffledPlayers.removeFirst()
            val player2 = shuffledPlayers.removeFirst()
            doubles.add(Doubles(player1, player2))
        }
        return doubles
    }

    fun generateMatches() {
        viewModelScope.launch {
            val players = _league.value.players
            val doubles = randomizeDouble(players)
            val shuffledDoubles = doubles.shuffled().toMutableList()
            val doublesCount = shuffledDoubles.size
            val matches = mutableListOf<Match>()

            val playerPlayingTwice: Player? = if (players.size.isOdd()) {
                shuffledDoubles.find { double ->
                    double.player1 == double.player2
                }?.player1
            } else {
                null
            }

            if (doublesCount.isOdd()) {
                val enemies = shuffledDoubles.indices.associateWith { 0 }.toMutableMap()
                var i = 0
                while (i < doublesCount) {
                    val double1 = shuffledDoubles[i]
                    var double2: Doubles
                    var j = i

                    val lastMatch = repository.getLastMatch(_league.value.id)
                    val newId = if(lastMatch != null) lastMatch.id + 1 else 1

                    while (enemies[i]!! < 2) {
                        j++
                        if (j >= shuffledDoubles.size) break // Menghindari index out of bounds
                        if (enemies[j] == 0 || i == doublesCount - 2) {
                            double2 = shuffledDoubles[j]
                        } else {
                            continue
                        }

                        // Pastikan tidak ada pemain yang melawan dirinya sendiri
                        if (playerPlayingTwice != null &&
                            (double1.player1 == playerPlayingTwice || double1.player2 == playerPlayingTwice) &&
                            (double2.player1 == playerPlayingTwice || double2.player2 == playerPlayingTwice)
                        ) {
                            continue // Lewati jika pemain akan melawan dirinya sendiri
                        }

                        if (enemies[i] == 0) {
                            matches.add(Match(newId, double1, double2))
                        } else {
                            matches.add(Match(newId, double2, double1))
                        }

                        enemies[i] = enemies[i]!! + 1
                        enemies[j] = enemies[j]!! + 1
                    }
                    i++
                }
            } else {
                while (shuffledDoubles.isNotEmpty()) {
                    val doubles1 = shuffledDoubles.first()
                    val doubles2 = shuffledDoubles.elementAtOrNull(1)

                    val lastMatch = repository.getLastMatch(_league.value.id)
                    val newId = if(lastMatch != null) lastMatch.id + 1 else 1

                    if (doubles2 != null) {
                        // Pastikan tidak ada pemain yang melawan dirinya sendiri
                        if (playerPlayingTwice != null &&
                            (doubles1.player1 == playerPlayingTwice || doubles1.player2 == playerPlayingTwice) &&
                            (doubles2.player1 == playerPlayingTwice || doubles2.player2 == playerPlayingTwice)
                        ) {
                            shuffledDoubles.shuffle() // Acak ulang jika ada masalah
                            continue
                        }

                        shuffledDoubles.removeFirst()
                        shuffledDoubles.removeFirst()
                        matches.add(Match(newId, doubles1, doubles2))
                    }
                }
            }

            while (!validateMatchSequence(matches)) {
                matches.shuffle()
            }
        }
    }

    private fun validateMatchSequence(matches: List<Match>): Boolean {
        for(i in 1 until matches.size) {
            val prevMatch = matches[i-1]
            val currentMatch = matches[i]

            if(matchHasSamePlayer(prevMatch, currentMatch)) {
                return false
            }
        }
        return true
    }

    private fun matchHasSamePlayer(match1: Match, match2: Match): Boolean {
        val match1Double = listOf(
            match1.doubles1,
            match1.doubles2,
        )
        val match2Double = listOf(
            match2.doubles1,
            match2.doubles2,
        )
        return match1Double.any { it in match2Double }
    }

    private fun timeNow(): Long {
        return System.currentTimeMillis()
    }

    private fun updateMatchStatus(
        matchId: Int,
        newStatus: String,
        newTimeStart: Long? = null,
        newTimeFinish: Long? = null
    ) {
        viewModelScope.launch {
            val leagueId = _league.value.id
            repository.setMatchStatus(leagueId, matchId, newStatus)
            newTimeStart?.let { repository.setMatchTimeStart(leagueId, matchId, it) }
            newTimeFinish?.let { repository.setMatchTimeStart(leagueId, matchId, it) }
        }
    }

    fun startMatch(matchId: Int) {
        updateMatchStatus(matchId, STARTED, newTimeStart = timeNow())
    }

    fun finishMatch(matchId: Int) {
        updateMatchStatus(matchId, FINISHED, newTimeFinish = timeNow())
    }
}

sealed class PlayerFormValidationResult {
    data object Valid : PlayerFormValidationResult()
    data object NameTooLong : PlayerFormValidationResult()
    data object NameIsBlank : PlayerFormValidationResult()
    data object InitialTooLong : PlayerFormValidationResult()
    data object InitialIsBlank : PlayerFormValidationResult()
}

sealed class MatchFormValidationResult {
    data object Valid : MatchFormValidationResult()
    data object PlayerIsBlank : MatchFormValidationResult()
    data object PlayerDuplicate : MatchFormValidationResult()
}

val SCHEDULED = "scheduled"
val STARTED = "started"
val FINISHED = "finished"