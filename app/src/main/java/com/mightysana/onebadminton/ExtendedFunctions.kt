package com.mightysana.onebadminton

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.navigation.NavController

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
