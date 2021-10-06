package com.streetbox.pos.ui.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
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
    var notificationChannel = "com.streetbox.pos"

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
        val mNotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(this, MainActivity::class.java)
                .putExtra("typeNotif", "listnotif")

        val pendingIntent = PendingIntent.getActivity(
                this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this)
                .setContentText(remoteMessage.notification!!.body)
                .setContentTitle(remoteMessage.notification!!.title)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setSound(defaultSoundUri)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setContentIntent(pendingIntent)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                    notificationChannel, "Notifku",
                    NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.description = ""
            notificationChannel.enableVibration(true)
            notificationChannel.setShowBadge(true)
//            notificationChannel.setAllowBubbles(true)
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            mNotificationManager.createNotificationChannel(notificationChannel)
        }

        // to diaplay notification in DND Mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = mNotificationManager.getNotificationChannel(notificationChannel)
            channel.canBypassDnd()
        }
    }
}