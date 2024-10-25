package com.mightysana.onebadminton.screens.league

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mightysana.onebadminton.isNull
import com.mightysana.onebadminton.isOdd
import com.mightysana.onebadminton.loss
import com.mightysana.onebadminton.model.LeagueRepository
import com.mightysana.onebadminton.properties.Doubles
import com.mightysana.onebadminton.properties.League
import com.mightysana.onebadminton.properties.Match
import com.mightysana.onebadminton.properties.Player
import com.mightysana.onebadminton.win
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

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

    private val _isAddMatchDialogVisible = MutableStateFlow(false)
    val isAddMatchDialogVisible: StateFlow<Boolean> = _isAddMatchDialogVisible

    private val _isFinishMatchDialogVisible = MutableStateFlow(false)
    val isFinishMatchDialogVisible: StateFlow<Boolean> = _isFinishMatchDialogVisible

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

    private val _finishedMatchId = MutableStateFlow(0)
    val finishedMatchId: StateFlow<Int> = _finishedMatchId

    fun setFinishedMatchId(id: Int) {
        _finishedMatchId.value = id
    }

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

    private fun scoreInvalid(matchId: Int): Boolean {
        val leagueRules = _league.value.rules
        val gamePoint = leagueRules.gamePoint
        val match = _league.value.matches[matchId]
        val score1 = match.score1
        val score2 = match.score2

        if(leagueRules.isDeuceEnabled) {
            if(score1 > gamePoint || score2 > gamePoint) {
                return abs(score1 - score2) != 2
            }
            if(score1 == gamePoint || score2 == gamePoint) {
                return abs(score1 - score2) <= 1
            }
        } else {
            if(score1 > gamePoint || score2 > gamePoint) {
                return true
            }
            if(score1 == score2) {
                return true
            }
        }
        if(score1 != gamePoint && score2 != gamePoint) {
            return true
        }
        return false
    }

    fun validateMatchScore(): MatchScoreValidationResult {
        return when {
            scoreInvalid(_finishedMatchId.value!!) -> MatchScoreValidationResult.Invalid
            else -> MatchScoreValidationResult.Valid
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
        _isAddMatchDialogVisible.value = show
    }

    private fun setShowFinishMatchDialog(show: Boolean) {
        _isFinishMatchDialogVisible.value = show
    }

    fun addScore1(matchId: Int) {
        viewModelScope.launch {
            val prevScore = _league.value.matches[matchId].score1
            val newScore = prevScore + 1
            val leagueId = _league.value.id
            repository.addScore1(leagueId, matchId, newScore)
        }
    }

    fun removeScore1(matchId: Int) {
        viewModelScope.launch {
            val prevScore = _league.value.matches[matchId].score1
            val newScore = if (prevScore > 0) prevScore - 1 else 0
            val leagueId = _league.value.id
            repository.removeScore1(leagueId, matchId, newScore)
        }
    }

    fun addScore2(matchId: Int) {
        viewModelScope.launch {
            val prevScore = _league.value.matches[matchId].score2
            val newScore = prevScore + 1
            val leagueId = _league.value.id
            repository.addScore2(leagueId, matchId, newScore)
        }
    }

    fun removeScore2(matchId: Int) {
        viewModelScope.launch {
            val prevScore = _league.value.matches[matchId].score2
            val newScore = if (prevScore > 0) prevScore - 1 else 0
            val leagueId = _league.value.id
            repository.removeScore2(leagueId, matchId, newScore)
        }
    }


    fun showAddMatchDialog() {
        setShowAddMatchDialog(true)
    }

    fun showFinishMatchDialog() {
        setShowFinishMatchDialog(true)
    }

    fun dismissAddMatchDialog() {
        setShowAddMatchDialog(false)
        resetPlayer()
    }

    fun dismissFinishMatchDialog() {
        setShowFinishMatchDialog(false)
//        resetFinishedMatchId()
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

    fun getPlayer(playerId: Int): Player {
        return _league.value.players[playerId]
    }

    fun addMatch() {
        viewModelScope.launch {
            val doubles1 = Doubles(_player1.value!!.id, _player2.value!!.id)
            val doubles2 = Doubles(_player3.value!!.id, _player4.value!!.id)

            val leagueId = _league.value.id

            val lastMatch = repository.getLastMatch(leagueId)
            val newId = if(lastMatch != null) lastMatch.id + 1 else 0
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
            doubles.add(Doubles(player1.id, player2.id))
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

            val playerIdPlayingTwice: Int? = if (players.size.isOdd()) {
                shuffledDoubles.find { double ->
                    double.player1Id == double.player2Id
                }?.player1Id
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
                        if (playerIdPlayingTwice != null &&
                            (double1.player1Id == playerIdPlayingTwice || double1.player2Id == playerIdPlayingTwice) &&
                            (double2.player1Id == playerIdPlayingTwice || double2.player2Id == playerIdPlayingTwice)
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
                        if (playerIdPlayingTwice != null &&
                            (doubles1.player1Id == playerIdPlayingTwice || doubles1.player2Id == playerIdPlayingTwice) &&
                            (doubles2.player1Id == playerIdPlayingTwice || doubles2.player2Id == playerIdPlayingTwice)
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
            newTimeStart?.let { repository.startMatch(leagueId, matchId, it) }
            newTimeFinish?.let { repository.finishMatch(leagueId, matchId, it) }
        }
    }

    fun startMatch(matchId: Int) {
        updateMatchStatus(matchId, STARTED, newTimeStart = timeNow())
    }

    fun finishMatch(matchId: Int) {
        val players = _league.value.players
        val match = _league.value.matches[matchId]
        val score1 = match.score1
        val score2 = match.score2
        val player1 = players[match.doubles1.player1Id]
        val player2 = players[match.doubles1.player2Id]
        val player3 = players[match.doubles2.player1Id]
        val player4 = players[match.doubles2.player2Id]
        val doubles1Id = listOf(player1.id, player2.id)
        val winner = if(score1 > score2) listOf(player1, player2) else listOf(player3, player4)
        val loser = if(score1 < score2) listOf(player1, player2) else listOf(player3, player4)

        winner.forEach {
            val newStats = it.win(
                if(it.id in doubles1Id) score1 else score2,
                if(it.id in doubles1Id) score2 else score1
            )
            updatePlayer(newStats)
        }
        loser.forEach {
            val newStats = it.loss(
                if(it.id in doubles1Id) score1 else score2,
                if(it.id in doubles1Id) score2 else score1
            )
            updatePlayer(newStats)
        }
        updateMatchStatus(matchId, FINISHED, newTimeFinish = timeNow())
        dismissFinishMatchDialog()
    }

    private fun updatePlayer(player: Player) {
        viewModelScope.launch {
            val leagueId = _league.value.id

            repository.updatePlayer(leagueId, player)
        }
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

sealed class MatchScoreValidationResult {
    data object Valid : MatchScoreValidationResult()
    data object Invalid : MatchScoreValidationResult()
}

val SCHEDULED = "scheduled"
val STARTED = "started"
val FINISHED = "finished"