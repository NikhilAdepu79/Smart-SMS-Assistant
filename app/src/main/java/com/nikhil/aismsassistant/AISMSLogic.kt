package com.nikhil.aismsassistant

import android.content.Context
import android.provider.ContactsContract
import android.util.Log

object AISMSLogic {

    private val tag = "AISMS_Logic"

    /**
     * Look up the contact name to make the AI sound more human.
     */
    private fun getContactName(context: Context, phoneNumber: String): String? {
        val contentResolver = context.contentResolver
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
        val selection = "${ContactsContract.CommonDataKinds.Phone.NUMBER} = ?"
        val selectionArgs = arrayOf(phoneNumber)

        var contactName: String? = null
        try {
            contentResolver.query(uri, projection, selection, selectionArgs, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        contactName = cursor.getString(nameIndex)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Contact lookup failed: ${e.message}")
        }
        return contactName
    }

    /**
     * Handles responses for missed calls.
     */
    fun handleMissedCall(context: Context, phoneNumber: String) {
        if (!AppPreferences.getAIModeState(context)) return

        val contactName = getContactName(context, phoneNumber)
        val userName = AppPreferences.getUserName(context)

        val greeting = if (contactName != null) "Hi $contactName," else "Hello,"

        // Professional English response for missed calls
        val message = "$greeting this is $userName's AI Assistant. $userName is currently unavailable—is there anything urgent I can help you with?"

        SmsSender.sendSms(context, phoneNumber, message)
        Log.d(tag, "Sent missed call auto-reply to $phoneNumber.")
    }

    /**
     * Handles responses for incoming text messages.
     */
    fun processIncomingSms(context: Context, senderNumber: String, messageBody: String) {
        if (!AppPreferences.getAIModeState(context)) return

        val lowerMsg = messageBody.lowercase()
        val userName = AppPreferences.getUserName(context)

        // 1. Check Custom Keywords from App (JSON/Gson)
        val customKeywords = AppPreferences.getCustomKeywords(context)
        for ((keyword, reply) in customKeywords) {
            if (lowerMsg.contains(keyword.lowercase())) {
                SmsSender.sendSms(context, senderNumber, reply)
                return
            }
        }

        // 2. Comprehensive English Conversation Rules
        when {
            lowerMsg.contains("where are you") || lowerMsg.contains("location") -> {
                SmsSender.sendSms(context, senderNumber, "$userName is out at the moment but will be back soon. I'll let them know you asked!")
            }
            lowerMsg.contains("call me") || lowerMsg.contains("ring me") -> {
                SmsSender.sendSms(context, senderNumber, "Sure! I've noted your request. $userName will call you back as soon as they are free.")
            }
            lowerMsg.contains("who is this") || lowerMsg.contains("who are you") -> {
                SmsSender.sendSms(context, senderNumber, "I am $userName's automated assistant. I'm helping manage messages while they are busy.")
            }
            lowerMsg.contains("urgent") || lowerMsg.contains("emergency") -> {
                SmsSender.sendSms(context, senderNumber, "Noted. I will try to alert $userName immediately regarding your urgent message.")
            }
            lowerMsg.contains("hi") || lowerMsg.contains("hello") || lowerMsg.contains("hey") -> {
                SmsSender.sendSms(context, senderNumber, "Hello! $userName is busy right now, but I am here to take a message for you.")
            }

            // 3. Telugu Language Natural Rules
            lowerMsg.contains("ekadha unnav") -> {
                SmsSender.sendSms(context, senderNumber, "$userName ippudu busy ga unnaru, thwaralone meeku reply istharu.")
            }
            lowerMsg.contains("call chey") || lowerMsg.contains("phone chey") -> {
                SmsSender.sendSms(context, senderNumber, "Sare, nenu $userName ki chepthanu. Free avvagane meeku call chestharu.")
            }
            lowerMsg.contains("em chesthunnav") -> {
                SmsSender.sendSms(context, senderNumber, "$userName ippudu work lo busy ga unnaru.")
            }
        }
    }
}