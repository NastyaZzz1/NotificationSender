package com.nastya.testappis74

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CreateNotifiViewModelFactory(
    private val notificationApi: NotificationApi,
)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateNotifiViewModel::class.java)) {
            return CreateNotifiViewModel(notificationApi) as T
        }
        throw IllegalArgumentException("Unkown ViewModel")
    }
}