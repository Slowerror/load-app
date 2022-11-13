package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.udacity.Constants.CHANNEL_ID
import com.udacity.Constants.KEY_FILE_NAME
import com.udacity.Constants.KEY_STATUS
import com.udacity.Constants.NOTIFICATION_ID

fun NotificationManager.sendNotification(title: String, status: String, appContext: Context) {
    val contentIntent = Intent(appContext, DetailActivity::class.java).apply {
        putExtra(KEY_FILE_NAME, title)
        putExtra(KEY_STATUS, status)
    }

    val pendingIntent = PendingIntent.getActivity(
        appContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val builder = NotificationCompat.Builder(appContext, CHANNEL_ID).apply {
        setSmallIcon(R.drawable.ic_assistant_black_24dp)
        setContentTitle(appContext.getString(R.string.notification_title))
        setContentText(appContext.getString(R.string.notification_description))
        addAction(
            R.drawable.ic_baseline_cloud_done_24,
            appContext.getString(R.string.notification_button),
            pendingIntent
        )
        priority = NotificationManager.IMPORTANCE_DEFAULT
    }.build()

    notify(NOTIFICATION_ID, builder)
}

fun NotificationManager.cancelNotification() {
    cancelAll()
}