package com.bsuuv.grocerymanager.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.bsuuv.grocerymanager.R
import com.bsuuv.grocerymanager.notifications.GroceryDayNotifier.Companion.NOTIFICATION_ID
import com.bsuuv.grocerymanager.ui.MainActivity

/**
 * A receiver for the alarm set by [GroceryDayNotifier]. Defines what happens when the alarm is
 * triggered.
 */
class NotificationReceiver : BroadcastReceiver() {

    companion object {
        const val PRIMARY_CHANNEL_ID = "primary_notification_channel"
    }

    private lateinit var mContext: Context

    override fun onReceive(context: Context?, intent: Intent?) {
        mContext = context!!
        sendNotification()
    }

    private fun sendNotification() {
        val notifBuilder = getNotificationBuilder()
        val notifManager =
            mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notifManager.notify(NOTIFICATION_ID, notifBuilder.build())
    }

    private fun getNotificationBuilder(): NotificationCompat.Builder {
        val notificationIntent = Intent(mContext, MainActivity::class.java)
        val notifPendingIntent = PendingIntent.getActivity(
            mContext,
            NOTIFICATION_ID,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        return NotificationCompat.Builder(mContext, PRIMARY_CHANNEL_ID)
            .setContentTitle(mContext.getString(R.string.notification_content_title))
            .setContentText(mContext.getString(R.string.notification_content_text))
            .setSmallIcon(R.drawable.ic_notification_primary)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(notifPendingIntent)
            .setAutoCancel(true)
    }

}