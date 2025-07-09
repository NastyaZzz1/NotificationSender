package com.nastya.testappis74

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    companion object {
        private const val TAG = "MyFirebaseMsgService"
        private const val CHANNEL_ID = "my_fcm_channel_id"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            handleDataMessage(remoteMessage.data)
        }

        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            handleNotification(it.title, it.body, remoteMessage.data)
        }
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        super.onNewToken(token)
    }

    private fun handleDataMessage(data: Map<String, String>) {
        val title = data["title"] ?: "Default Title"
        val message = data["message"] ?: "Default Message"
        sendNotification(title, message, data)
    }

    private fun handleNotification(title: String?, body: String?, data: Map<String, String>) {
        sendNotification(title ?: "Default Title", body ?: "Default Message", data)
    }

    private fun sendNotification(title: String, message: String, data: Map<String, String>) {
        val groupKey = "com.nastya.testappis74.NOTIFICATION_GROUP"

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("notification_data", data.toString())

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setGroup(groupKey)
            .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)

        val summaryNotification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("У вас новые уведомления")
            .setContentText("Нажмите, чтобы посмотреть")
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setGroup(groupKey)
            .setGroupSummary(true)
            .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            CHANNEL_ID,
            "FCM Channel",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
        notificationManager.notify(0, summaryNotification.build())
    }
}
