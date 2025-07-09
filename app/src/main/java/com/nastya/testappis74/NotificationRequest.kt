package com.nastya.testappis74

import com.google.gson.annotations.SerializedName

data class NotificationRequest(
    @SerializedName("token") val token: String,
    @SerializedName("title") val title: String,
    @SerializedName("message") val message: String
)