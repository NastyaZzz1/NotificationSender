import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging

class FcmTokenManager {

   inline fun getToken(crossinline callback: (String) -> Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("FcmTokenManager", "FCM Token: $task.result")
                callback(task.result)
            }
        }
    }
}