package com.mightysana.onebadminton.properties

data class Player(
    val id: Int = 0,
    val name: String = "",
    val initial: String = "",
    var points: Int = 0,
    var matches: Int = 0,
    var wins: Int = 0,
    var losses: Int = 0,
    var scoreIn: Int = 0,
    var scoreOut: Int = 0
)