package com.streetbox.pos.ui.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
//import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.streetbox.pos.R
import com.streetbox.pos.ui.main.MainActivity
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class MyFirebaseMessagingService : FirebaseMessagingService() {

    val TAG = "Service"
    var notificationChannel = "com.streetbox.pos"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("Tag", "toke refress :  $token")
//        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
//            Log.d("FMS_TOKEN", it.token)
//        }
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            val paramsObject = remoteMessage.data
            val jsonObject = JSONObject(paramsObject as Map<String, String>)
            println("respon Json Notif $jsonObject")
            val body = jsonObject.get("body")
            val title = jsonObject.get("title")
            println("respon Json Notif body $body")

            sendNotification(title = title.toString(), body = body.toString())
            EventBus.getDefault().post(MessageEvent())
        }
        else {
//            println("respon Notif ${remoteMessage.notification!!.imageUrl}")
            Log.d(TAG, "From: " + remoteMessage.from)
            Log.d("respon Data enduser", "${remoteMessage.data}")
            sendNotification(
                remoteMessage.notification!!.title.toString(),
                remoteMessage.notification!!.body.toString()
            )
        }
    }


    private fun sendNotification(title: String, body: String) {
        val mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val qIntent = Intent(this, MainActivity::class.java)
            .putExtra("typeNotif", "listnotif")
//            .putExtra("key", remoteMessage.data["typeNotif"])


        val contentIntentQuest = PendingIntent.getActivity(
            this, 0,
            qIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val contentView = RemoteViews(packageName, R.layout.layout_popup_notif)
        contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher)
        contentView.setTextViewText(R.id.tvTitlePopupNotif, body)


        val notificationSoundURI: Uri =
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, notificationChannel)
            .setColor(ContextCompat.getColor(this, R.color.green))
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

    fun getBitmapfromUrl(imageUrl: String?): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
            null
        }
    }
}