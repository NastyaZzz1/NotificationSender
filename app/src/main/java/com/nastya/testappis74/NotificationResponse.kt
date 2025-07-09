package com.nastya.testappis74

import com.google.gson.annotations.SerializedName

data class NotificationResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("messageId") val messageId: String?
)