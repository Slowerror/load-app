package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.udacity.Constants.CHANNEL_ID
import com.udacity.Constants.CHANNEL_NAME
import com.udacity.Constants.GLIDE_URL
import com.udacity.Constants.REPOSITORY_URL
import com.udacity.Constants.RETROFIT_URL
import com.udacity.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager

    private lateinit var url: String
    private lateinit var fileName: String
    private lateinit var status: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        getUrl()
        createChanel()

        binding.layoutContentMain.customButton.setOnClickListener {
            if (::url.isInitialized) {
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
                    notificationManager.sendNotification(fileName, status, this@MainActivity)

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


    private fun createChanel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChanel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            notificationChanel.enableLights(true)
            notificationChanel.enableVibration(false)
            notificationChanel.description = getString(R.string.channel_description)

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChanel)
        }
    }


}
