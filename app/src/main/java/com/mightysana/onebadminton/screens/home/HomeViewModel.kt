package com.mightysana.onebadminton.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mightysana.onebadminton.model.LeagueRepository
import com.mightysana.onebadminton.properties.League
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

    private val _isDialogVisible = MutableStateFlow(false)
    val isDialogVisible: StateFlow<Boolean> = _isDialogVisible

    private val _leagueName = MutableStateFlow("")
    val leagueName: StateFlow<String> = _leagueName

    fun setLeagueName(name: String) {
        _leagueName.value = name
    }

    private fun setDialogVisibility(isVisible: Boolean) {
        _isDialogVisible.value = isVisible
    }

    fun showDialog() {
        setDialogVisibility(true)
    }

    fun dismissDialog() {
        setDialogVisibility(false)
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

    fun addLeague(name: String) {
        viewModelScope.launch {
            val lastLeague = repository.getLastLeague()
            val newId = if(lastLeague != null) lastLeague.id + 1 else 1

            val newLeague = League(newId, name)
            repository.addLeague(newLeague)
           fetchLeagues()
        }
    }

}