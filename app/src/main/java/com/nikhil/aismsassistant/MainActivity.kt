package com.nikhil.aismsassistant

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {

    private val tag = "AISMS_MainActivity"
    private val permissionRequestCode = 100

    private lateinit var aiModeSwitch: Switch
    private lateinit var statusTextView: TextView
    private lateinit var userNameInput: EditText
    private lateinit var saveNameButton: Button

    private val REQUIRED_PERMISSIONS = arrayOf(
        android.Manifest.permission.READ_PHONE_STATE,
        android.Manifest.permission.READ_CALL_LOG,
        android.Manifest.permission.SEND_SMS,
        android.Manifest.permission.RECEIVE_SMS,
        android.Manifest.permission.READ_SMS,
        android.Manifest.permission.READ_CONTACTS
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI widgets
        aiModeSwitch = findViewById(R.id.aiModeSwitch)
        statusTextView = findViewById(R.id.statusTextView)
        userNameInput = findViewById(R.id.userNameInput)
        saveNameButton = findViewById(R.id.saveNameButton)

        Log.d(tag, "MainActivity created. Checking permissions...")
        checkAndRequestPermissions()

        // Load existing name and set it in the input box
        val savedName = AppPreferences.getUserName(this)
        userNameInput.setText(savedName)

        // Load initial AI mode state
        val currentAIModeState = AppPreferences.getAIModeState(this)
        aiModeSwitch.isChecked = currentAIModeState
        updateStatusText(currentAIModeState)

        // Save name button listener
        saveNameButton.setOnClickListener {
            val newName = userNameInput.text.toString().trim()
            if (newName.isNotEmpty()) {
                AppPreferences.setUserName(this, newName)
                Toast.makeText(this, "AI name updated to: $newName", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show()
            }
        }

        aiModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            AppPreferences.setAIModeState(this, isChecked)
            updateStatusText(isChecked)
            Toast.makeText(this, "AI Mode ${if (isChecked) "ON" else "OFF"}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateStatusText(isAiModeOn: Boolean) {
        statusTextView.text = "AI Mode Status: ${if (isAiModeOn) "ON" else "OFF"}"
        statusTextView.setTextColor(ContextCompat.getColor(this, if (isAiModeOn) android.R.color.holo_green_dark else android.R.color.holo_red_dark))
    }

    private fun checkAndRequestPermissions() {
        val permissionsToRequest = ArrayList<String>()
        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission)
            }
        }
        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), permissionRequestCode)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionRequestCode && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            Toast.makeText(this, "Permissions Granted!", Toast.LENGTH_SHORT).show()
        }
    }
}