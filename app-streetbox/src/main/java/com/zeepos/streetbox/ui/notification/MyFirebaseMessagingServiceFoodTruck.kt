package com.zeepos.streetbox.ui.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.zeepos.models.ConstVar
import com.zeepos.streetbox.R
import com.zeepos.streetbox.ui.main.MainActivity
import com.zeepos.streetbox.ui.operator.main.OperatorFTActivity
import com.zeepos.utilities.SharedPreferenceUtil
import org.json.JSONObject

class MyFirebaseMessagingServiceFoodTruck : FirebaseMessagingService() {

    val TAG = "Service"
    var notificationChannel = "com.zeepos.streetbox"
    var title:String = "title"
    var body:String = "body"

    override fun onNewToken(token: String) {
        Log.d("respon Tag", "toke refress :  $token")

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            Log.d("respon food FMS_TOKEN", it.token)
        }

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            val paramsObject = remoteMessage.data
            val jsonObject = JSONObject(paramsObject as Map<String, String>)
            println("respon Json Notif $jsonObject")
             body = jsonObject.get("body").toString()
            title = jsonObject.get("title").toString()
            println("respon Json Notif body $body")
            sendNotification(title = title, body = body as String)
        }else{
            sendNotification(title = remoteMessage.notification!!.title!!, body = remoteMessage.notification!!.body!!)
        }


//        }
    }


    private fun sendNotification(title: String, body: String) {
        val mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        var qIntent = Intent()
        if(title.equals("New Home Visit Order")) {
           qIntent = Intent(this, MainActivity::class.java)
               .putExtra("typeNotif", title)
               .putExtra("body",body)
        }else{
             qIntent = Intent(this, OperatorFTActivity::class.java)
                .putExtra("typeNotifOperator", title)
                 .putExtra("body",body)
        }


        val contentIntentQuest = PendingIntent.getActivity(
            this, 0,
            qIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationSoundURI: Uri =
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, notificationChannel)
            .setColor(ContextCompat.getColor(this, R.color.orange))
            .setContentTitle(body)
            .setContentText(title)
            .setDefaults(Notification.DEFAULT_ALL)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setChannelId(notificationChannel)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setSound(notificationSoundURI)


        notificationBuilder.setContentIntent(contentIntentQuest)
        mNotificationManager.notify(1, notificationBuilder.build())

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