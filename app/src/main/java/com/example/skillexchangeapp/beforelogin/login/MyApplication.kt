package com.example.skillexchangeapp.beforelogin.login

import android.app.Application
import com.example.skillexchangeapp.afterlogin.ImageTransformerSingletonObj
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )

        // ✅ Init singleton with contentResolver
        ImageTransformerSingletonObj.init(contentResolver)
    }
}
