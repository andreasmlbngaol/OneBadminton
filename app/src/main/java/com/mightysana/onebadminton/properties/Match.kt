package com.mightysana.onebadminton.properties

import com.mightysana.onebadminton.screens.league.SCHEDULED

data class Match(
    val id: Int = 0,
    val doubles1: Doubles = Doubles(),
    val doubles2: Doubles = Doubles(),
    val score1: Int = 0,
    val score2: Int = 0,
    var status: String = SCHEDULED,
    val timeStart: Long = 0L,
    val timeFinish: Long = 0L,
    val durationInMillis: Long = 0L
)