package com.softwill.alpha.dashboard

import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.softwill.alpha.R
import com.softwill.alpha.chat.FirebaseUtil
import com.softwill.alpha.databinding.ActivityDashboardBinding


class DashboardActivity : AppCompatActivity(){

    private lateinit var binding: ActivityDashboardBinding
    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard);

        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()

        val adapter = MyDashboardTabAdapter(this, supportFragmentManager, 1)
        binding.viewPager.adapter = adapter

        binding.viewPager.setCurrentItem(1, true)
        FirebaseApp.initializeApp(this)

        getFCMToken()

        // Handle FCM notification data
        intent?.extras?.let { bundle ->
            for (key in bundle.keySet()) {
                val value = bundle[key]
                Log.d("FCM_DATA", "Key: $key, Value: $value")
            }
        }
    }

    fun getFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task: Task<String?> ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d(TAG, "getFCMToken: $token")
//                FirebaseUtil.currentUserDetails().update("fcmToken", token)
            }
        }
    }



    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            doubleBackToExitPressedOnce = false
        }, 1000)
    }




}