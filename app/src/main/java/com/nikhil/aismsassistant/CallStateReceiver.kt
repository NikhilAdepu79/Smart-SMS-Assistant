// app/src/main/java/com/nikhil/aismsassistant/CallStateReceiver.kt
package com.nikhil.aismsassistant

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import java.util.Timer
import java.util.TimerTask

class CallStateReceiver : BroadcastReceiver() {

    private val tag = "AISMS_CallReceiver"
    private val MISSED_CALL_THRESHOLD_MS = 25 * 1000L // 25 seconds in milliseconds

    companion object {
        private var lastState = TelephonyManager.CALL_STATE_IDLE
        private var isIncoming = false
        private var currentIncomingNumber: String? = null // This will hold the most recent valid number
        private var callTimer: Timer? = null // Timer for 25s threshold
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(tag, "onReceive: Action = ${intent.action}")

        val aiModeOn = AppPreferences.getAIModeState(context)
        if (!aiModeOn) {
            Log.d(tag, "AI Mode is OFF. Ignoring call event.")
            // Ensure any pending timer is cancelled if AI mode just turned off
            callTimer?.cancel()
            callTimer = null
            return
        }

        if (intent.action == Intent.ACTION_NEW_OUTGOING_CALL) {
            // For outgoing calls, the number is directly available
            currentIncomingNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER)
            Log.d(tag, "Outgoing call to: $currentIncomingNumber")
            isIncoming = false
        } else {
            val stateStr = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            val incomingNumberFromIntent = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
            var state = 0

            when (stateStr) {
                TelephonyManager.EXTRA_STATE_IDLE -> state = TelephonyManager.CALL_STATE_IDLE
                TelephonyManager.EXTRA_STATE_OFFHOOK -> state = TelephonyManager.CALL_STATE_OFFHOOK
                TelephonyManager.EXTRA_STATE_RINGING -> state = TelephonyManager.CALL_STATE_RINGING
            }

            Log.d(tag, "Call State: $stateStr, Incoming Number from Intent: $incomingNumberFromIntent")

            // IMPORTANT: Update currentIncomingNumber if a valid number comes in during any state change
            if (incomingNumberFromIntent != null && incomingNumberFromIntent.isNotBlank()) {
                currentIncomingNumber = incomingNumberFromIntent
                Log.d(tag, "Updated currentIncomingNumber in companion object to: $currentIncomingNumber")
            }

            onCallStateChanged(context, state) // Now, onCallStateChanged doesn't need to receive the number
        }
    }

    private fun onCallStateChanged(context: Context, state: Int) { // Removed 'number: String?' parameter
        if (lastState == state) {
            return
        }

        when (state) {
            TelephonyManager.CALL_STATE_RINGING -> {
                isIncoming = true
                Log.d(tag, "Call RINGING. Stored number (may be null initially): $currentIncomingNumber")

                // Cancel any previous timer to avoid multiple triggers for the same call
                callTimer?.cancel()
                callTimer = null

                // Start the 25-second timer
                callTimer = Timer()
                callTimer?.schedule(object : TimerTask() {
                    override fun run() {
                        // CRITICAL: Get the latest currentIncomingNumber when the timer actually executes
                        val numberToUse = currentIncomingNumber
                        if (lastState == TelephonyManager.CALL_STATE_RINGING && isIncoming && numberToUse != null && numberToUse.isNotBlank()) {
                            Log.d(tag, "25 seconds passed. Call likely missed. Triggering SMS for: $numberToUse")
                            AISMSLogic.handleMissedCall(context, numberToUse) // Use the non-null number here
                        } else {
                            Log.w(tag, "Timer fired, but conditions not met to send SMS. LastState: $lastState, isIncoming: $isIncoming, numberToUse: $numberToUse")
                        }
                        callTimer?.cancel() // Ensure timer is always cancelled after it runs
                        callTimer = null
                    }
                }, MISSED_CALL_THRESHOLD_MS)
            }
            TelephonyManager.CALL_STATE_OFFHOOK -> {
                // Call answered or dialing/active. Cancel any pending missed call timer.
                callTimer?.cancel()
                callTimer = null
                Log.d(tag, "Call OFFHOOK (answered/active).")
            }
            TelephonyManager.CALL_STATE_IDLE -> {
                // Call ended (missed, answered, or rejected)
                callTimer?.cancel() // Ensure timer is cancelled if call goes idle
                callTimer = null

                if (lastState == TelephonyManager.CALL_STATE_RINGING && isIncoming) {
                    Log.d(tag, "Call IDLE after RINGING. Potentially a quick missed call or rejected: $currentIncomingNumber")
                    // Note: If a call is quickly missed (less than 25s), the timer won't send an SMS based on current logic.
                    // If you want to send an SMS for *any* missed call regardless of duration, we'd need another rule here.
                }
                Log.d(tag, "Call IDLE. Resetting currentIncomingNumber and isIncoming.")
                currentIncomingNumber = null // Reset for the next call
                isIncoming = false // Reset incoming flag
            }
        }
        lastState = state // Update last state
    }
}