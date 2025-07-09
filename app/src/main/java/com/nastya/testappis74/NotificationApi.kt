package com.nastya.testappis74

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface NotificationApi {
    @POST("send-custom-notification")
    suspend fun sendNotification(
        @Body request: NotificationRequest
    ): Response<NotificationResponse>
}