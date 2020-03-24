package com.mahmoud.udacitynotifier

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        switch_service.setOnClickListener {
            if ((it as Switch).isChecked) {
                val serviceIntent = Intent(this, MyService::class.java)
                serviceIntent.action = MyService.ACTION_START_SERVICE
                ContextCompat.startForegroundService(this, serviceIntent)
                Toast.makeText(this, "Started successfully", Toast.LENGTH_LONG).show()

            } else {
                val serviceIntent = Intent(this, MyService::class.java)
                serviceIntent.action = MyService.ACTION_STOP_SERVICE

                stopService(serviceIntent)
                Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show()

            }
        }
    }

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        switch_service.isChecked = isServiceRunning(MyService::class.java)

    }
}
