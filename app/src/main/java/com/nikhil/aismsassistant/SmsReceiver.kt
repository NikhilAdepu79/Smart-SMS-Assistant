// app/src/main/java/com/nikhil/aismsassistant/SmsReceiver.kt
package com.nikhil.aismsassistant

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log

class SmsReceiver : BroadcastReceiver() {

    private val tag = "AISMS_SmsReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(tag, "onReceive: Action = ${intent.action}")

        val aiModeOn = AppPreferences.getAIModeState(context)
        if (!aiModeOn) {
            Log.d(tag, "AI Mode is OFF. Ignoring incoming SMS.")
            return
        }

        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION == intent.action) {
            val smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            for (message in smsMessages) {
                val senderNum = message.originatingAddress
                val messageBody = message.messageBody
                Log.d(tag, "Incoming SMS from: $senderNum, Message: '$messageBody'")

                if (senderNum != null && messageBody != null) {
                    AISMSLogic.processIncomingSms(context, senderNum, messageBody)
                }
            }
        }
    }
}