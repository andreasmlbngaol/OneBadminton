package com.mightysana.onebadminton.properties

data class Couple(
    val player1: Player = Player(),
    val player2: Player = Player(),
    val score: Int = 0
)