package com.mightysana.onebadminton.model

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.mightysana.onebadminton.properties.League
import com.mightysana.onebadminton.properties.Player
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LeagueRepository @Inject constructor() {
    private val database: DatabaseReference = Firebase.database.reference.child("leagues")

    suspend fun getLeague(id: Int): League {
        return database.child(id.toString()).get().await().getValue(League::class.java)!!
    }

    suspend fun getLeagues(): List<Pair<Int, League>> {
        val snapshot = database.get().await()  // Ambil snapshot
        return snapshot.children.mapNotNull {
            val league = it.getValue(League::class.java)
            if (league != null) {
                Pair(it.key!!.toInt(), league)
            } else {
                null
            }
        }
    }

    suspend fun getLastLeague(): League? {
        val leagues = getLeagues()
        return leagues.maxByOrNull { it.first }?.second

    }

    suspend fun addLeague(league: League) {
        val id = league.id
        database.child(id.toString()).setValue(league).await()
    }

    private suspend fun getPlayers(leagueId: Int): List<Pair<Int, Player>> {
        val snapshot = database.child(leagueId.toString()).child("players").get().await()  // Ambil snapshot
        return snapshot.children.mapNotNull {
            val player = it.getValue(Player::class.java)
            if (player != null) {
                Pair(it.key!!.toInt(), player)
            } else {
                null
            }
        }
    }

    suspend fun getLastPlayer(leagueId: Int): Player? {
        val players = getPlayers(leagueId)
        Log.d("LeagueRepository", "Players: $players")
        return players.maxByOrNull { it.first }?.second
    }

    suspend fun addPlayer(player: Player, leagueId: Int) {
        val id = player.id
        database.child(leagueId.toString()).child("players").child(id.toString()).setValue(player).await()
    }

}