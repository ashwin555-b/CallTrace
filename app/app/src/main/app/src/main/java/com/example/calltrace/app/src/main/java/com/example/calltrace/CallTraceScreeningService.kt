
package com.example.calltrace

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.telecom.Call
import android.telecom.CallScreeningService
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.geocoding.PhoneNumberOfflineGeocoder
import java.util.Locale

class CallTraceScreeningService : CallScreeningService() {

    companion object {
        private const val CHANNEL_ID = "calltrace_caller_id"
        private const val NOTIFICATION_ID = 4401
    }

    override fun onScreenCall(details: Call.Details) {
        val number = details.handle?.schemeSpecificPart.orEmpty()

        // Allow the call. CallTrace does not block or reject calls.
        val response = CallResponse.Builder()
            .setDisallowCall(false)
            .setRejectCall(false)
            .setSkipCallLog(false)
            .setSkipNotification(false)
            .build()

        respondToCall(details, response)

        if (number.isNotBlank()) {
            showLookupNotification(number)
        }
    }

    private fun showLookupNotification(number: String) {
        val util = PhoneNumberUtil.getInstance()

        val parsed = try {
            util.parse(number, "IN")
        } catch (_: Exception) {
            null
        }

        val valid = parsed != null && util.isPossibleNumber(parsed)

        val country = if (valid) {
            val code = util.getRegionCodeForNumber(parsed)
            if (code != null) {
                Locale("", code).getDisplayCountry(Locale.ENGLISH)
            } else {
                "Unknown"
            }
        } else {
            "Unknown"
        }

        val place = if (valid) {
            try {
                PhoneNumberOfflineGeocoder.getInstance()
                    .getDescriptionForNumber(
                        parsed,
                        Locale.ENGLISH
                    )
                    .ifBlank { "Not available" }
            } catch (_: Exception) {
                "Not available"
            }
        } else {
            "Not available"
        }

        val message = """
            Number: $number
            Country: $country
            Region/place: $place
        """.trimIndent()

        val manager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Caller identification",
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
        }

        val openApp = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or
                PendingIntent.FLAG_IMMUTABLE
        )

        val notification = if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        ) {
            Notification.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.sym_call_incoming)
                .setContentTitle("CallTrace • Caller information")
                .setContentText("Country: $country • Place: $place")
                .setStyle(
                    Notification.BigTextStyle().bigText(message)
                )
                .setContentIntent(openApp)
                .setAutoCancel(true)
                .build()
        } else {
            Notification.Builder(this)
                .setSmallIcon(android.R.drawable.sym_call_incoming)
                .setContentTitle("CallTrace • Caller information")
                .setContentText("Country: $country • Place: $place")
                .setStyle(
                    Notification.BigTextStyle().bigText(message)
                )
                .setContentIntent(openApp)
                .setAutoCancel(true)
                .build()
        }

        manager.notify(NOTIFICATION_ID, notification)
    }
}
