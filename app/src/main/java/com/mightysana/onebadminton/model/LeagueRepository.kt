package com.mightysana.onebadminton.model

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.mightysana.onebadminton.properties.League
import com.mightysana.onebadminton.properties.Match
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

    fun observeLeagues(onUpdate: (List<Pair<Int, League>>) -> Unit) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Konversi data snapshot menjadi List<Pair<Int, League>>
                val leaguesData = dataSnapshot.children.mapNotNull { leagueSnapshot ->
                    val league = leagueSnapshot.getValue(League::class.java)
                    league?.let { Pair(leagueSnapshot.key!!.toInt(), it) } // Menggunakan key sebagai ID
                }
                onUpdate(leaguesData) // Memanggil callback dengan data terbaru
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("LeagueRepository", "Error fetching leagues: ${databaseError.message}")
            }
        })
    }

    fun observeLeague(leagueId: Int, onUpdate: (League) -> Unit) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val league = dataSnapshot.child(leagueId.toString()).getValue(League::class.java)
                league?.let {
                    onUpdate(it)
                    Log.d("LeagueViewModel", "League updated: $it")
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("LeagueRepository", "Error fetching leagues: ${databaseError.message}")
            }
        })
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

    private suspend fun getMatches(leagueId: Int): List<Pair<Int, Match>> {
        val snapshot = database.child(leagueId.toString()).child("matches").get().await()  // Ambil snapshot
        return snapshot.children.mapNotNull {
            val match = it.getValue(Match::class.java)
            if (match != null) {
                Pair(it.key!!.toInt(), match)
            } else {
                null
            }
        }
    }

//    suspend fun getPlayer(leagueId: Int, playerId: Int): Player? {
//        val players = getPlayers(leagueId)
//        return players.find { it.first == playerId }?.second
//    }

    suspend fun getLastPlayer(leagueId: Int): Player? {
        val players = getPlayers(leagueId)
        Log.d("LeagueRepository", "Players: $players")
        return players.maxByOrNull { it.first }?.second
    }

    suspend fun getLastMatch(leagueId: Int): Match? {
        val matches = getMatches(leagueId)
        return matches.maxByOrNull { it.first }?.second

    }

    suspend fun addPlayer(player: Player, leagueId: Int) {
        val id = player.id
        database.child(leagueId.toString()).child("players").child(id.toString()).setValue(player).await()
    }

    suspend fun addMatch(match: Match, leagueId: Int) {
        val id = match.id
        database.child(leagueId.toString()).child("matches").child(id.toString()).setValue(match).await()
    }

    suspend fun setMatchStatus(leagueId: Int, matchId: Int, newStatus: String) {
        database.child(leagueId.toString()).child("matches").child(matchId.toString()).child("status").setValue(newStatus).await()
    }

    suspend fun startMatch(leagueId: Int, matchId: Int, time: Long) {
        database.child(leagueId.toString()).child("matches").child(matchId.toString()).child("timeStart").setValue(time).await()
    }

    suspend fun finishMatch(leagueId: Int, matchId: Int, timeFinish: Long) {
        database.child(leagueId.toString()).child("matches").child(matchId.toString()).child("timeFinish").setValue(timeFinish).await()
        val timeStart = database.child(leagueId.toString()).child("matches").child(matchId.toString()).child("timeStart").get().await().getValue(Long::class.java)
        val matchDuration = timeFinish - timeStart!!
        database.child(leagueId.toString()).child("matches").child(matchId.toString()).child("durationInMillis").setValue(matchDuration).await()
    }

    suspend fun addScore1(leagueId: Int, matchId: Int, newScore: Int) {
        database.child("$leagueId").child("matches").child(matchId.toString()).child("score1").setValue(newScore).await()
    }

    suspend fun removeScore1(leagueId: Int, matchId: Int, newScore: Int) {
        database.child("$leagueId").child("matches").child(matchId.toString()).child("score1").setValue(newScore).await()
    }

    suspend fun addScore2(leagueId: Int, matchId: Int, newScore: Int) {
        database.child("$leagueId").child("matches").child(matchId.toString()).child("score2").setValue(newScore).await()
    }

    suspend fun removeScore2(leagueId: Int, matchId: Int, newScore: Int) {
        database.child("$leagueId").child("matches").child(matchId.toString()).child("score2").setValue(newScore).await()
    }

    suspend fun updatePlayer(leagueId: Int, player: Player) {
        database.child("$leagueId").child("players").child(player.id.toString()).setValue(player).await()
    }
}