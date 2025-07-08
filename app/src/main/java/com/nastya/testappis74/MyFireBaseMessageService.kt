package com.nastya.testappis74

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "MyFirebaseMsgService"
        private const val CHANNEL_ID = "my_fcm_channel_id"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                Log.d(TAG, "messageId: ${remoteMessage.messageId}")
//                remoteMessage.messageId?.let { messageId ->
//                    val api = Retrofit.Builder()
//                        .baseUrl("http://10.0.2.2:3000")
//                        .addConverterFactory(GsonConverterFactory.create())
//                        .build()
//                        .create(ApiService::class.java)
//                    api.confirmDelivery(DeliveryConfirmation("projects/testappis74/messages/" + messageId))
//                }
//            } catch (e: Exception) {
//                Log.e("FCM", "Ошибка подтверждения доставки", e)
//            }
//        }

        if (remoteMessage.data.isNotEmpty()) {
//            logEvent("notification_received_data")
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            handleDataMessage(remoteMessage.data)
        }

        remoteMessage.notification?.let {
//            logEvent("notification_received_visible")
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

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "FCM Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
        notificationManager.notify(0, summaryNotification.build())
    }
}

interface ApiService {
    @POST("confirm-delivery")
    suspend fun confirmDelivery(@Body body: DeliveryConfirmation)
}

data class DeliveryConfirmation(
    @SerializedName("messageId")
    val messageId: String
)
