package com.bsuuv.grocerymanager.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.graphics.Color
import android.os.Build
import com.bsuuv.grocerymanager.R

class NotificationChannelCreator(private val mContext: Context) {

    companion object {
        const val PRIMARY_CHANNEL_ID = "primary_notification_channel"
    }

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