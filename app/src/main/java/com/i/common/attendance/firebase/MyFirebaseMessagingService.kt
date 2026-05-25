package com.i.common.attendance.firebase

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.i.common.attendance.utils.EncryptedPrefHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var sharedPref : EncryptedPrefHelper
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Log the device tokenQ
        Log.e("FCM Token", token)
        if(sharedPref.getFCMToken() != token){
            sharedPref.saveFCMToken(token)
            /*CoroutineScope(Dispatchers.IO).launch {
                sendTokenToServer(token)
            }*/
        }
        // You can also send the token to your server for further processing
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)


    }
}