package com.udacity

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.DownloadManager.*
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.apache.commons.io.FilenameUtils

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private var fileUrl = ""
    private var fileName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        notificationManager =
            getSystemService(NotificationManager::class.java) as NotificationManager

        //Create a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = R.string.notification_description.toString()
            notificationChannel.apply {
                setShowBadge(false)
            }

            notificationManager.createNotificationChannel(notificationChannel)
        }

        //Register the Broadcast Receiver to alert the application when the file is downloaded
        registerReceiver(receiver, IntentFilter(ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            Thread {
                download()
            }.start()
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(EXTRA_DOWNLOAD_ID, -1)

            if (id == downloadID) {
                buildNotification(
                    this@MainActivity,
                    resources.getString(R.string.notification_description),
                    fileUrl,
                    fileName
                )
            }
        }
    }

    @SuppressLint("Range")
    private fun download() {
        if (downloading_options_radio.checkedRadioButtonId == -1) {
//            Toast.makeText(
//                this,
//                "Please select one option to continue downloading...",
//                Toast.LENGTH_LONG
//            ).show()
            custom_button.changeButtonState(ButtonState.Completed)
        } else {
            fileUrl =
                findViewById<RadioButton>(downloading_options_radio.checkedRadioButtonId)
                    .contentDescription.toString()
            fileName = FilenameUtils.getName(fileUrl)
            Log.d("selectedUrl", fileUrl)
            Log.d("fileName", fileName)

            val request =
                Request(Uri.parse(fileUrl))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                downloadManager.enqueue(request)
        }
    }

    companion object {
        const val CHANNEL_ID = "channelId"
        const val CHANNEL_NAME = "channelName"
    }

    fun buildNotification(
        application: MainActivity,
        messageBody: String,
        fileUrl: String,
        fileName: String
    ) {
        val contentIntnt = Intent(application, MainActivity::class.java)
        val contentPendingIntnt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(application, 0, contentIntnt, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(
                application,
                0,
                contentIntnt,
                PendingIntent.FLAG_ONE_SHOT
            )
        }

        val detailsIntnt = Intent(application, DetailActivity::class.java)
        detailsIntnt.putExtra(DetailActivity.FILE_URL, fileUrl)
        detailsIntnt.putExtra(DetailActivity.FILE_NAME, fileName)

        val pendingIntnt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(application, 0, detailsIntnt, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(
                application,
                0,
                detailsIntnt,
                PendingIntent.FLAG_ONE_SHOT
            )
        }
        val builder = NotificationCompat.Builder(
            application,
            MainActivity.CHANNEL_ID
        )

        val notificationImg = BitmapFactory.decodeResource(
            application.resources,
            R.drawable.ic_launcher_foreground
        )
        val bigPicStyle =
            NotificationCompat.BigPictureStyle().bigPicture(notificationImg).bigLargeIcon(null)

        builder.setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(application.getString(R.string.notification_title))
            .setContentIntent(contentPendingIntnt)
            .setContentText(messageBody)
            .setStyle(bigPicStyle)
            .setLargeIcon(notificationImg)
            .addAction(
                R.drawable.ic_launcher_foreground,
                "Open details",
                pendingIntnt
            ).setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(application)
        notificationManager.notify(1001, builder.build())
    }
}
