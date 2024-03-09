package com.softwill.alpha.chat

import android.annotation.SuppressLint
import android.content.Intent
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.softwill.alpha.notification.NotificationActivity

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FCMNotificationService : FirebaseMessagingService(){

    // Initialize the counter
    private var notificationCount = 0

    override fun onNewToken(token: String) {
        // Handle the token (send it to your server, etc.)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Handle the received message
        // Increment the counter when a new notification is received
        notificationCount++
        // Handle the notification and display it to the user

        // Check if the message contains data payload
        remoteMessage.data.isNotEmpty().let {
            // Extract destination activity information
            val destinationActivity = remoteMessage.data["destination"]

            // Launch appropriate activity based on destination
            when (destinationActivity) {
                "ChatActivity" -> {
                    // Launch ChatActivity
                    val intent = Intent(this, ChatActivity::class.java).apply {
                        // Pass any additional data if needed
                        // For example: putExtra("key", "value")
                    }
                    startActivity(intent)
                }
                "MainActivity" -> {
                    // Launch MainActivity
                    val intent = Intent(this, NotificationActivity::class.java).apply {
                        // Pass any additional data if needed
                    }
                    startActivity(intent)
                }
                // Add more cases for other activities if needed
                else -> {
                    // Handle unknown destination or default case
                    val intent = Intent(this, ChatActivity::class.java).apply {
                        // Pass any additional data if needed
                        // For example: putExtra("key", "value")
                    }
                    startActivity(intent)
                }
            }
        }
    }

}
