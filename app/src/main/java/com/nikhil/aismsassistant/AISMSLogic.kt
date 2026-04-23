package com.nikhil.aismsassistant

import android.content.Context
import android.provider.ContactsContract
import android.util.Log

object AISMSLogic {

    private val tag = "AISMS_Logic"

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
                    if (nameIndex != -1) contactName = cursor.getString(nameIndex)
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Error looking up contact name: ${e.message}")
        }
        return contactName
    }

    fun handleMissedCall(context: Context, phoneNumber: String) {
        if (AppPreferences.getAIModeState(context) && phoneNumber.isNotBlank()) {
            val contactName = getContactName(context, phoneNumber)
            val userName = AppPreferences.getUserName(context)

            val greeting = if (contactName != null) "Hi $contactName," else "Hello,"
            val message = "$greeting this is $userName's AI assistant. $userName is busy right now—can I help?"

            SmsSender.sendSms(context, phoneNumber, message)
        }
    }

    fun processIncomingSms(context: Context, senderNumber: String, messageBody: String) {
        if (!AppPreferences.getAIModeState(context)) return

        val lowerCaseMessage = messageBody.lowercase()
        val userName = AppPreferences.getUserName(context)

        val customKeywords = AppPreferences.getCustomKeywords(context)
        for ((keyword, reply) in customKeywords) {
            if (lowerCaseMessage.contains(keyword.lowercase())) {
                SmsSender.sendSms(context, senderNumber, reply)
                return
            }
        }

        if (lowerCaseMessage.contains("ekadha unnav")) {
            SmsSender.sendSms(context, senderNumber, "$userName is busy right now.")
            return
        }

        if (lowerCaseMessage.contains("call chey")) {
            SmsSender.sendSms(context, senderNumber, "nenu $userName ki inform chesta.")
            return
        }

        if (lowerCaseMessage.contains("call me")) {
            SmsSender.sendSms(context, senderNumber, "$userName is busy right now, I will inform them.")
            return
        }
    }
}