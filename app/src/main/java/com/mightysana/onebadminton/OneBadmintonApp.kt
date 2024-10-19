package com.mightysana.onebadminton

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class OneBadmintonApp: Application() {
    override fun onCreate() {
        super.onCreate()
        // Inisialisasi Firebase
        FirebaseApp.initializeApp(this)
    }
}