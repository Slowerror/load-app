package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.udacity.databinding.ActivityMainBinding



class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    private lateinit var url: String
    private lateinit var fileName: String
    private lateinit var status: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        getUrl()
        createChanel()

        binding.layoutContentMain.customButton.setOnClickListener {
            if (::url.isInitialized) {
                binding.layoutContentMain.customButton.setupButtonState(ButtonState.Loading)
                download()
            } else {
                Toast.makeText(
                    this@MainActivity,
                    R.string.toast_message_select_file,
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
    }

    private fun getUrl() {
        binding.layoutContentMain.downloadList.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.downloadFromGlide -> {
                    url = GLIDE_URL
                    fileName = getString(R.string.glide_description_download)
                }
                R.id.downloadFromRepository -> {
                    url = REPOSITORY_URL
                    fileName = getString(R.string.repository_description_download)
                }
                R.id.downloadFromRetrofit -> {
                    url = RETROFIT_URL
                    fileName = getString(R.string.retrofit_description_download)
                }
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            id?.let {
                val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                val query = DownloadManager.Query()
                query.setFilterById(it)
                val cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                    status = if (DownloadManager.STATUS_SUCCESSFUL == cursor.getInt(index)) {
                        getString(R.string.success_download_status)
                    } else {
                        getString(R.string.fail_download_status)
                    }
                    sendNotification()
                    binding.layoutContentMain.customButton.setupButtonState(ButtonState.Completed)
                }
            }

        }
    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(fileName)
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID = downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }


    private fun sendNotification() {
        val contentIntent = Intent(applicationContext, DetailActivity::class.java).apply {
            putExtra(KEY_FILE_NAME, fileName)
            putExtra(KEY_STATUS, status)
        }

        pendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(
            applicationContext,
            CHANNEL_ID
        ).apply {
            setSmallIcon(R.drawable.ic_assistant_black_24dp)
            setContentTitle(getString(R.string.notification_title))
            setContentText(getString(R.string.notification_description))
            setContentIntent(pendingIntent)
            addAction(
                R.drawable.ic_baseline_cloud_done_24,
                getString(R.string.notification_button),
                pendingIntent
            )
            setAutoCancel(true)
            priority = NotificationManager.IMPORTANCE_DEFAULT
        }.build()

        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, builder)
    }

    private fun createChanel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChanel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                enableLights(true)
                enableVibration(false)
                description = getString(R.string.channel_description)
            }

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChanel)

        }
    }

    companion object {
        private const val GLIDE_URL = "https://github.com/bumptech/glide"
        private const val RETROFIT_URL = "https://github.com/square/retrofit"
        private const val REPOSITORY_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"

        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"

        private const val NOTIFICATION_ID = 0
        private const val CHANNEL_ID = "channelId"
        private const val CHANNEL_NAME = "Download notification"

        const val KEY_FILE_NAME = "key file"
        const val KEY_STATUS = "key status"
    }

}
