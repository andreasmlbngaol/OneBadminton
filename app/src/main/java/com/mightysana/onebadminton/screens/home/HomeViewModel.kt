package com.mightysana.onebadminton.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mightysana.onebadminton.isNaturalNumber
import com.mightysana.onebadminton.model.LeagueRepository
import com.mightysana.onebadminton.properties.League
import com.mightysana.onebadminton.properties.LeagueRules
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: LeagueRepository
): ViewModel() {
    private val _leagues = MutableStateFlow<Map<Int, League>>(emptyMap())
    val leagues: StateFlow<Map<Int, League>> = _leagues

    private val _isAddMatchDialogVisible = MutableStateFlow(false)
    val isAddMatchDialogVisible: StateFlow<Boolean> = _isAddMatchDialogVisible

    private val _leagueName = MutableStateFlow("")
    val leagueName: StateFlow<String> = _leagueName

    private val _gamePoint = MutableStateFlow(0)
    val gamePoint: StateFlow<Int> = _gamePoint

    private val _isDeuceEnabled = MutableStateFlow(true)
    val isDeuceEnabled: StateFlow<Boolean> = _isDeuceEnabled

    fun setDeuceEnabled(isEnabled: Boolean) {
        _isDeuceEnabled.value = isEnabled
    }

    fun setGamePoint(point: Int) {
        _gamePoint.value = point
    }

    fun setLeagueName(name: String) {
        _leagueName.value = name
    }

    private fun resetLeagueName() {
        setLeagueName("")
    }

    private fun resetGamePoint() {
        setGamePoint(0)
    }

    private fun resetIsDeuceEnabled() {
        setDeuceEnabled(true)
    }

    private fun resetForm() {
        resetLeagueName()
        resetGamePoint()
        resetIsDeuceEnabled()
    }

    private fun setDialogVisibility(isVisible: Boolean) {
        _isAddMatchDialogVisible.value = isVisible
    }

    fun showDialog() {
        setDialogVisibility(true)
        resetForm()
    }

    fun dismissDialog() {
        setDialogVisibility(false)
        resetForm()
    }

    init {
        Log.d("HomeViewModel", "init called")  // Debug log
//        fetchLeagues()
        observeLeagues()
    }

    private fun observeLeagues() {
        repository.observeLeagues { leaguesData ->
            _leagues.value = leaguesData.toMap() // Update _leagues dengan data terbaru
        }
    }

    private fun fetchLeagues() {
        viewModelScope.launch {
            try {
                val leaguesData = repository.getLeagues()
                _leagues.value = leaguesData.toMap()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    fun addLeague() {
        viewModelScope.launch {
            val name = _leagueName.value
            val gamePoint = _gamePoint.value
            val isDeuceEnabled = _isDeuceEnabled.value

            val lastLeague = repository.getLastLeague()
            val newId = if(lastLeague != null) lastLeague.id + 1 else 1

            val newLeague = League(newId, name, rules = LeagueRules(gamePoint, isDeuceEnabled))
            repository.addLeague(newLeague)
            dismissDialog()
        }
    }

    private fun isNameLengthValid(): Boolean {
        return _leagueName.value.length <= 20
    }

    private fun isNameBlank(): Boolean {
        return _leagueName.value.isBlank()
    }

    private fun isGamePointValid(): Boolean {
        return _gamePoint.value.isNaturalNumber()
    }

    private fun isGamePointBlank(): Boolean {
        return _gamePoint.value == 0
    }

    fun validateLeagueForm(): LeagueFormValidationResult {
        return when {
            isNameBlank() -> LeagueFormValidationResult.NameIsBlank
            !isNameLengthValid() -> LeagueFormValidationResult.NameTooLong
            isGamePointBlank() -> LeagueFormValidationResult.GamePointIsBlank
            !isGamePointValid() -> LeagueFormValidationResult.GamePointTooLow
            else -> LeagueFormValidationResult.Valid
        }
    }
}

sealed class LeagueFormValidationResult() {
    data object Valid: LeagueFormValidationResult()
    data object NameIsBlank: LeagueFormValidationResult()
    data object NameTooLong: LeagueFormValidationResult()
    data object GamePointIsBlank: LeagueFormValidationResult()
    data object GamePointTooLow: LeagueFormValidationResult()
}