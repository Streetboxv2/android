package com.streetbox.pos.ui.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.streetbox.pos.R
import com.streetbox.pos.ui.main.MainActivity
import org.greenrobot.eventbus.EventBus


class MyFirebaseMessagingService : FirebaseMessagingService(){

    val TAG = "Service"
    override fun onNewToken(token: String) {
        Log.d("Tag","toke refress :  $token")

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            Log.d("FMS_TOKEN", it.token)
        }

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        Log.d(TAG, "From: " + remoteMessage!!.from)
        Log.d("FROM", remoteMessage.getFrom())
        sendNotification(remoteMessage)
        EventBus.getDefault().post(MessageEvent())

    }


    private fun sendNotification(remoteMessage:RemoteMessage) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this)
            .setContentText(remoteMessage.notification!!.body)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_star)
            .setSound(defaultSoundUri)
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            .setContentIntent(pendingIntent)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }
}