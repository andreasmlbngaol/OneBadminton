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

    init {
        Log.d("HomeViewModel", "init called")  // Debug log
        fetchLeagues()
    }

    private fun fetchLeagues() {
        Log.d("HomeViewModel", "fetchLeagues called")  // Debug log
        viewModelScope.launch {
            Log.d("HomeViewModel", "Fetching leagues...")  // Debug log
            try {
                Log.d("HomeViewModel", "Fetching leagues from repository...")  // Debug log
                val leaguesData = repository.getLeagues()
                Log.d("HomeViewModel", "Leagues data: $leaguesData")  // Log hasil data dari repository
                _leagues.value = leaguesData.toMap()
                Log.d("HomeViewModel", "Leagues fetched: ${_leagues.value}")  // Debug log
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching leagues", e)  // Error log
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