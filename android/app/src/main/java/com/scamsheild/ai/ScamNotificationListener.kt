package com.scamsheild.ai

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class ScamNotificationListener : NotificationListenerService() {

    override fun onListenerConnected() {
        super.onListenerConnected()

        Log.d("ScamShield", "Notification Listener connected!")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        if (sbn == null) return

        val extras = sbn.notification.extras

        val title = extras.getString(Notification.EXTRA_TITLE) ?: ""
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""

        val message = "$title $text"

        if (looksSuspicious(message)) {
            Log.d(
                "ScamShield",
                "Suspicious notification detected: $message"
            )
        } else {
            Log.d(
                "ScamShield",
                "Normal notification ignored: $message"
            )
        }
    }

    private fun looksSuspicious(message: String): Boolean {

        val suspiciousWords = listOf(
            "urgent",
            "verify",
            "verification",
            "kyc",
            "blocked",
            "block",
            "otp",
            "password",
            "account",
            "bank",
            "payment",
            "refund",
            "prize",
            "winner",
            "click",
            "link",
            "suspended",
            "immediately"
        )

        val lowerMessage = message.lowercase()

        return suspiciousWords.any { word ->
            lowerMessage.contains(word)
        }
    }
}