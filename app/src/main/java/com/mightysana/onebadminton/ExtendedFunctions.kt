package com.mightysana.onebadminton

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.navigation.NavController
import com.mightysana.onebadminton.properties.Player

fun NavController.navigateAndPopUp(route: String, popUp: String) {
    this.navigate(route) {
        launchSingleTop = true
        popUpTo(popUp) { inclusive = true }
    }
}

fun Context.toastMessage(
    @StringRes message: Int,
    duration: Int = Toast.LENGTH_SHORT
) {
    Toast.makeText(
        this,
        this.getString(message),
        duration
    ).show()
}

fun Int.isEven(): Boolean = this % 2 == 0
fun Int.isOdd(): Boolean = !this.isEven()

fun <T> T?.isNull(): Boolean {
    return this == null
}

fun Long.toDisplayableTime(): String {
    val seconds = (this / 1000) % 60
    val minutes = (this / (1000 * 60) % 60)
    return String.format("%02d.%02d", minutes, seconds)
}

const val WIN_POINT = 1
const val LOSS_POINT = 0

fun Player.win(scoreIn: Int, scoreOut: Int): Player {
    return Player(
        id = this.id,
        name = this.name,
        initial = this.initial,
        points = this.points + WIN_POINT,
        matches = this.matches + 1,
        wins = this.wins + 1 ,
        losses = this.losses,
        scoreIn = this.scoreIn + scoreIn,
        scoreOut = this.scoreOut + scoreOut
    )
}

fun Player.loss(scoreIn: Int, scoreOut: Int): Player {
    return Player(
        id = this.id,
        name = this.name,
        initial = this.initial,
        points = this.points + LOSS_POINT,
        matches = this.matches + 1,
        wins = this.wins,
        losses = this.losses + 1,
        scoreIn = this.scoreIn + scoreIn,
        scoreOut = this.scoreOut + scoreOut
    )
}

fun Int.isNaturalNumber(): Boolean = this > 0