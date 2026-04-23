package com.nikhil.aismsassistant

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object AppPreferences {

    private const val PREFS_NAME = "AISMS_PREFS"
    private const val KEY_AI_MODE_ON = "AI_MODE_ON"
    private const val KEY_CUSTOM_KEYWORDS = "CUSTOM_KEYWORDS"
    private const val KEY_USER_NAME = "USER_NAME"
    private const val TAG = "AISMS_AppPrefs"

    private val gson = Gson()

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun setAIModeState(context: Context, isOn: Boolean) {
        getSharedPreferences(context).edit().putBoolean(KEY_AI_MODE_ON, isOn).apply()
        Log.d(TAG, "AI Mode set to: $isOn")
    }

    fun getAIModeState(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(KEY_AI_MODE_ON, false)
    }

    fun setUserName(context: Context, name: String) {
        getSharedPreferences(context).edit().putString(KEY_USER_NAME, name).apply()
        Log.d(TAG, "User name set to: $name")
    }

    fun getUserName(context: Context): String {
        return getSharedPreferences(context).getString(KEY_USER_NAME, "the user") ?: "the user"
    }

    fun saveCustomKeywords(context: Context, keywordMap: Map<String, String>) {
        val jsonString = gson.toJson(keywordMap)
        getSharedPreferences(context).edit().putString(KEY_CUSTOM_KEYWORDS, jsonString).apply()
    }

    fun getCustomKeywords(context: Context): Map<String, String> {
        val jsonString = getSharedPreferences(context).getString(KEY_CUSTOM_KEYWORDS, null)
        if (jsonString == null) {
            // Default keyword set for first-time use
            return mapOf(
                "urgent" to "He’ll contact you ASAP.",
                "joke" to "Sorry, the AI doesn't laugh 😄",
                "exam" to "He’s studying now; he’ll ping you later."
            )
        }

        return try {
            val type = object : TypeToken<Map<String, String>>() {}.type
            gson.fromJson(jsonString, type)
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing custom keywords: ${e.message}")
            mapOf()
        }
    }
}