package com.mahmoud.udacitynotifier

import android.annotation.SuppressLint
import android.app.*
import android.app.Notification.DEFAULT_VIBRATE
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.PixelFormat
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.webkit.ValueCallback
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.FLAG_SHOW_LIGHTS
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import java.util.*
import kotlin.concurrent.fixedRateTimer


class MyService : Service() {
    companion object {
        val CHANNEL_ID = "UdacityServiceChannel"
        val CHANNEL_ID_NEW_QUESTION = "UdacityServiceChannelNew"

        val ACTION_START_STOP_LISTEN = "start_stop_listen"
        val ACTION_START_SERVICE = "start_service"
        val ACTION_STOP_SERVICE = "stop_service"

    }
    @Volatile  var isListening = false

    var timer:Timer?=null
    var mWindowManager: WindowManager? = null
    var wv: WebView? = null
    var view: LinearLayout? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent != null) {
            when (intent.action) {
                ACTION_START_SERVICE -> {
                    startService()
                }
                ACTION_STOP_SERVICE -> {
                    isListening=false
                    stopService()
                }
                ACTION_START_STOP_LISTEN -> {
                    //check if activate or deactivate listening
                    if (!isListening) activateListening() else deActivateListening()
                }

            }
        }
        return START_NOT_STICKY
    }

    private fun startService() {

        startForeground(1, getNotification("activate"))
        //do heavy work on a background thread
        initViews()
    }

    //stopSelf();
    private fun activateListening() {
        startForeground(1, getNotification("activate"))
        startListening()
       // Log.e("service", "state listening")

        isListening = true
    }

    private fun deActivateListening() {
        startForeground(1, getNotification("deactivate"))
       // Log.e("service", "state not listening")
        timer?.cancel()
        isListening = false
    }

    private fun getNotification(action: String): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val startIntent = Intent(this, MyService::class.java)
        startIntent.action = ACTION_START_STOP_LISTEN
        val PendingStartIntent = PendingIntent.getService(this, 0, startIntent, 0)
        val title = if (action.equals("activate")) "Deactivate" else "Activate"
        val icon = if (action.equals("activate")) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
        val startAction = NotificationCompat.Action.Builder(
            icon,
            title,
            PendingStartIntent
        ).build()

        val stopIntent = Intent(this, MyService::class.java)
        stopIntent.action = ACTION_STOP_SERVICE
        val PendingstopIntent = PendingIntent.getService(this, 0, stopIntent, 0)
        val stopAction = NotificationCompat.Action.Builder(
            android.R.drawable.ic_delete,
            "Exit",
            PendingstopIntent
        ).build()
        val content = if (action.equals("activate")) "Active" else "Inactive"
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Udacity Notifier")
            .setContentText(content)
            .setColor(
                ContextCompat.getColor(
                    this,
                    com.mahmoud.udacitynotifier.R.color.colorPrimary
                )
            )
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    resources,
                    com.mahmoud.udacitynotifier.R.drawable.ic_launcher_foreground
                )
            )
            .setSmallIcon(com.mahmoud.udacitynotifier.R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .addAction(startAction)
            .addAction(stopAction)
            .build()
        return notification
    }

    private fun notifyUser(content:String){
        Log.e("notifyUser is=", isListening.toString())

        if (!isListening) return
        val notificationNewQuestion = NotificationCompat.Builder(this, CHANNEL_ID_NEW_QUESTION)
            .setContentTitle("Udacity Dashboard Notifier")
            .setContentText(content)
            .setContentIntent(
                PendingIntent.getService(
                    applicationContext,
                    0,
                    Intent(),
                    PendingIntent.FLAG_UPDATE_CURRENT
                ))
            .setColor(
                ContextCompat.getColor(
                    this,
                    com.mahmoud.udacitynotifier.R.color.colorPrimary
                )
            )
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    resources,
                    com.mahmoud.udacitynotifier.R.drawable.ic_launcher_foreground
                )
            )
            .setAutoCancel(true)
           // .setDefaults( DEFAULT_VIBRATE or FLAG_SHOW_LIGHTS )
            .setSmallIcon(com.mahmoud.udacitynotifier.R.drawable.ic_notification)

            .build()
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(222,notificationNewQuestion)
        }


    }

    private fun initViews() {
        mWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        var params: WindowManager.LayoutParams? = null
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
            )

            params.gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
            params.x = 0
            params.y = 0
            params.width = 0
            params.height = 0


        } else {
            params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
            )
        }
        params.gravity = Gravity.TOP or Gravity.LEFT
        params.x = 0
        params.y = 0
        params.width = 0
        params.height = 0

        view = LinearLayout(this)
        view?.layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )
        wv = WebView(this)
        wv?.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        wv?.settings?.javaScriptEnabled = true
        wv?.settings?.domStorageEnabled = true
        wv?.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
               // Log.e("onPageFinished is=", isListening.toString())

                if (!isListening) return  //just in case user stopped service while webview is loading
                wv?.postDelayed({
                    wv?.evaluateJavascript(
                        "(function() { return (document.getElementsByTagName('html')[0].innerText); })();",
                        ValueCallback<String?> { html ->
                            //Log.e("evaluateJavascript is=", isListening.toString())

                            if (!isListening) return@ValueCallback  //just in case user stopped service while webview is loading

                            when {
                                html?.contains("Agreement", false)!! -> {
                                    //should notify user to reset or accept
                                    notifyUser("Couldn't reach dashboard , please relogin and accept agreement ")
                                }
                                html.contains("Earnings", false) -> {
                                    Log.e(
                                        "result",
                                        "inside dashboard"
                                    ) // we r in , check for questions
                                    if (html.contains("Answer", true)) {
                                        //notify user
                                        notifyUser("You may have new Question")

                                    }
                                }

                            }

                        })
                }, 10000) //just to make sure webpage loaded completely

            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
               // Log.e("siteURL", url)
                view?.loadUrl(url)
                return true
            }

        }
        view?.addView(wv)
        startListening()
        mWindowManager?.addView(view, params)
    }

    private fun stopService() {
       // Log.e("stopservice is=", isListening.toString())

        // mWindowManager?.removeView(view)
        if (view?.parent != null) {
            mWindowManager?.removeView(view)
        }
        timer?.cancel()
        isListening = false
        stopSelf()
    }

    private fun startListening() {
        timer=fixedRateTimer(
            "udacity",
            false,
            0.toLong(),
            5*60*1000L) {
            wv?.post {
                wv?.loadUrl("https://mentor-dashboard.udacity.com/queue/overview")


            }
        }
        isListening = true
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Udacity Notifier",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)

            val sound =Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + BuildConfig.APPLICATION_ID + "/" + R.raw.tone)
            val attributes=AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            val serviceChannel2 = NotificationChannel(
                CHANNEL_ID_NEW_QUESTION,
                "Udacity Dasboard New Question",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            Log.e("sound",sound.toString())
            serviceChannel2.enableVibration(true)
            serviceChannel2.vibrationPattern= longArrayOf(0,500)
            serviceChannel2.setSound(sound,attributes)

            manager.createNotificationChannel(serviceChannel2)
        }
    }

    override fun onDestroy() {
        isListening=false
        if (view?.parent != null) {
            mWindowManager?.removeView(view)
        }
        timer?.cancel()
        super.onDestroy()
    }
}
