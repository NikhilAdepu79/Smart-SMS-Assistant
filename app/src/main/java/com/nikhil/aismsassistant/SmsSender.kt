// app/src/main/java/com/nikhil/aismsassistant/SmsSender.kt
package com.nikhil.aismsassistant

import android.content.Context
import android.telephony.SmsManager
import android.util.Log

object SmsSender {

    private val tag = "AISMS_SmsSender"

    fun sendSms(context: Context, phoneNumber: String, message: String) {
        try {
            val smsManager = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                context.getSystemService(SmsManager::class.java)
            } else {
                @Suppress("DEPRECATION")
                SmsManager.getDefault()
            }

            val parts = smsManager.divideMessage(message)
            smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null)

            Log.d(tag, "SMS sent to $phoneNumber: '$message'")
        } catch (e: Exception) {
            Log.e(tag, "Failed to send SMS to $phoneNumber: ${e.message}", e)
        }
    }
}