package com.nastya.testappis74

import FcmTokenManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CreateNotifiViewModel(
    private val notificationApi: NotificationApi,
) : ViewModel() {
    private val fcmTokenManager = FcmTokenManager()
    private val _notificationState = MutableStateFlow<NotificationState>(NotificationState.Idle)
    val notificationState: StateFlow<NotificationState> = _notificationState

    sealed class NotificationState {
        object Idle : NotificationState()
        object Loading : NotificationState()
        data class Success(val messageId: String) : NotificationState()
        data class Error(val message: String) : NotificationState()
    }

    fun sendNotification(title: String, message: String) {
        _notificationState.value = NotificationState.Loading
        try {
            fcmTokenManager.getToken() { token ->
                viewModelScope.launch {
                    val response = notificationApi.sendNotification(
                        NotificationRequest(token, title, message)
                    )

                    if (response.isSuccessful && response.body()?.success == true) {
                        _notificationState.value =
                            NotificationState.Success(response.body()!!.messageId!!)
                    } else {
                        _notificationState.value =
                            NotificationState.Error("Ошибка сервера: ${response.code()}")
                    }
                }
            }
        } catch (e: Exception) {
            _notificationState.value =
                NotificationState.Error("Ошибка: ${e.localizedMessage}")
        }
    }
}