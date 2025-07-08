package com.nastya.testappis74

import android.os.Build
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.POST

interface AnalyticsApi {
    @POST("send-notification")
    suspend fun trackEvent(@Body event: AnalyticsEvent)
}

data class AnalyticsEvent(
    val eventType: String,
    val deviceId: String = Build.ID
)

fun logEvent(eventType: String) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val api = Retrofit.Builder()
                .baseUrl("http://localhost:3000")
                .build()
                .create(AnalyticsApi::class.java)

            api.trackEvent(AnalyticsEvent(eventType))
        } catch (e: Exception) {
            Log.e("Analytics", "Ошибка: ${e.message}")
        }
    }
}