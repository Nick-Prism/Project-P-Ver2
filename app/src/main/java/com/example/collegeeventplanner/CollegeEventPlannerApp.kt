package com.example.collegeeventplanner

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CollegeEventPlannerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        testFirebaseConnection()
    }

    private fun testFirebaseConnection() {
        try {
            FirebaseFirestore.getInstance().collection("test").document("connection")
                .set(mapOf("timestamp" to System.currentTimeMillis()))
                .addOnSuccessListener {
                    Log.d("FirebaseTest", "Firebase connection successful")
                }
                .addOnFailureListener { e ->
                    Log.e("FirebaseTest", "Firebase connection failed", e)
                }
        } catch (e: Exception) {
            Log.e("FirebaseTest", "Firebase initialization error", e)
        }
    }
}
