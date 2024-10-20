package com.mightysana.onebadminton.properties

data class Match(
    val doubles1: Doubles = Doubles(),
    val doubles2: Doubles = Doubles(),
    val score1: Int = 0,
    val score2: Int = 0
)