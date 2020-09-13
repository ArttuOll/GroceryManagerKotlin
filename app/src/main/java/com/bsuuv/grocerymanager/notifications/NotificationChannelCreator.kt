package com.bsuuv.grocerymanager.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.graphics.Color
import android.os.Build
import com.bsuuv.grocerymanager.R

/**
 * A logic class responsible for creating the notification channel for the grocery day notification.
 */
class NotificationChannelCreator(private val mContext: Context) {

    companion object {
        const val PRIMARY_CHANNEL_ID = "primary_notification_channel"
    }

    /**
     * Creates the primary notification channel of this app. This method can be
     * safely called multiple times, since trying to create a notification channel
     * that already exists causes no action (see [Android documentation on notifications](https://developer.android.com/training/notify-user/channels#importance))
     *
     * Since notification channels are required only on SDKs higher than 26, on
     * lower SDKs this method does nothing.
     */
    fun createNotificationChannel() {
        val notifManager = mContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (sdkOreoOrHigher()) {
            val primaryChannel = buildPrimaryChannel()!!
            notifManager.createNotificationChannel(primaryChannel)
        }
    }

    private fun buildPrimaryChannel(): NotificationChannel? {
        if (sdkOreoOrHigher()) {
            val channelName = mContext.getString(R.string.notifchan_primary_name)
            val primaryChannel = NotificationChannel(
                PRIMARY_CHANNEL_ID,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            setChannelAttributes(primaryChannel)
            return primaryChannel
        }
        return null
    }

    private fun setChannelAttributes(primaryChannel: NotificationChannel) {
        if (sdkOreoOrHigher()) {
            primaryChannel.enableLights(true)
            primaryChannel.lightColor = Color.GREEN
            primaryChannel.enableVibration(true)
            primaryChannel.description = mContext.getString(R.string.notifchan_primary_description)
        }
    }

    private fun sdkOreoOrHigher() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
}