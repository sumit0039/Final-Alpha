package com.softwill.alpha.chat

import android.annotation.SuppressLint
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FCMNotificationService : FirebaseMessagingService(){
    override fun onNewToken(token: String) {
        // Handle the token (send it to your server, etc.)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Handle the received message
    }
}
