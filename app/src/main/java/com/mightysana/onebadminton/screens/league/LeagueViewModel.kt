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

    private fun resetName() {
        setName("")
    }

    private fun resetInitial() {
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

    private fun isNameBlank(): Boolean {
        return _name.value.isBlank()
    }

    private fun isInitialBlank(): Boolean {
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
        Log.d("LeagueViewModel", "Fetched league: ${_league.value}")
    }

    fun addPlayer(name: String, initial: String, leagueId: Int) {
        viewModelScope.launch {
            val lastPlayer = repository.getLastPlayer(leagueId)
            val newId = if(lastPlayer != null) lastPlayer.id + 1 else 1
            val newPlayer = Player(newId, name, initial)
            resetNameAndInitial()
            repository.addPlayer(newPlayer, leagueId)
            fetchLeague(leagueId)
//            dismissAddPlayerDialog()
        }
    }
}

sealed class FormValidationResult {
    object Valid : FormValidationResult()       // Jika form valid
    object NameTooLong : FormValidationResult() // Jika name terlalu panjang
    object NameIsBlank : FormValidationResult() // Jika name kosong
    object InitialTooLong : FormValidationResult() // Jika initial terlalu panjang
    object InitialIsBlank : FormValidationResult() // Jika initial kosong
}
