package com.mightysana.onebadminton.properties

data class League(
    val id: Int = 0,
    val name: String = "",
    val matches: List<Match> = emptyList(),
    val players: List<Player> = emptyList()
)