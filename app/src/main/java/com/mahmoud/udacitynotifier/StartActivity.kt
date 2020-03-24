package com.mahmoud.udacitynotifier

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View.VISIBLE
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_start.*


class StartActivity : AppCompatActivity() {
    var ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469
    var permissionGranted = false
    var loggedIn = false
    lateinit var webview: WebView
    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (getSharedPreferences("prefs", Context.MODE_PRIVATE).getBoolean("AllDone", false)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        setContentView(R.layout.activity_start)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            button_grant_permission.isEnabled = false  //already granted
            permissionGranted = true
        }else {
            if (Settings.canDrawOverlays(this)) {
                button_grant_permission.isEnabled = false  //already granted
                permissionGranted = true

            }
        }
        webview = WebView(this)
        button_done.setOnClickListener {
            if (!permissionGranted) {
                Toast.makeText(this, "Can't function without permission", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (!loggedIn) {
                Toast.makeText(this, "Log in process seems not complete ", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            getSharedPreferences("prefs", Context.MODE_PRIVATE).edit().putBoolean("AllDone", true)
                .apply()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            button_grant_permission.visibility = VISIBLE
            textView3.visibility = VISIBLE
        }

        button_grant_permission.setOnClickListener {
            grantPermission()
        }




        button_login.setOnClickListener {

            startActivityForResult(Intent(this, LoginActivity::class.java), 202)

        }


    }


    @SuppressLint("NewApi")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                permissionGranted = true
                button_grant_permission.isEnabled = false  //already granted

            }
        } else if (requestCode == 202) {
            loggedIn = resultCode == Activity.RESULT_OK
        }
    }

    fun grantPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE)
            }
        }
    }
}
