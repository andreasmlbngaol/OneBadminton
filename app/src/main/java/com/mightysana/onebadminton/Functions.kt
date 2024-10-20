package com.mightysana.onebadminton

import androidx.navigation.NavController

fun NavController.navigateAndPopUp(route: String, popUp: String) {
    this.navigate(route) {
        launchSingleTop = true
        popUpTo(popUp) { inclusive = true }
    }
}
