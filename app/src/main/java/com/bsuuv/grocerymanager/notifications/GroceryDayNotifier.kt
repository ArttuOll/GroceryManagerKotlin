package com.bsuuv.grocerymanager.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.os.SystemClock
import com.bsuuv.grocerymanager.util.SharedPreferencesHelper

class GroceryDayNotifier(
    private val mContext: Context,
    private val mSharedPrefsHelper: SharedPreferencesHelper,
    daysUntilGroceryDay: Int
) {

    companion object {
        const val NOTIFICATION_ID = 0
    }

    private val mAlarmManager: AlarmManager
    private var mDaysToNotif: Long

    init {
        val mChannelCreator = NotificationChannelCreator(mContext)
        mChannelCreator.createNotificationChannel()
        mDaysToNotif = calculateDaysToNotif(daysUntilGroceryDay)
        mAlarmManager = mContext.getSystemService(ALARM_SERVICE) as AlarmManager
    }

    private fun calculateDaysToNotif(daysUntilGroceryDay: Int) =
        AlarmManager.INTERVAL_DAY * daysUntilGroceryDay

    fun scheduleGroceryDayNotification() {
        val groceryDaysChangedListener = createOnSharedPrefsChangedListener()
        val sharedPreferences = mSharedPrefsHelper.sharedPreferences
        sharedPreferences.registerOnSharedPreferenceChangeListener(groceryDaysChangedListener)
    }

    private fun createOnSharedPrefsChangedListener(): SharedPreferences.OnSharedPreferenceChangeListener {
        return SharedPreferences.OnSharedPreferenceChangeListener { _, preferenceKey ->
            if (groceryDaysChanged(preferenceKey)) {
                val notificationPendingIntent = createPendingIntent()
                val triggerTime = calculateTriggerTime()
                setAlarm(notificationPendingIntent, triggerTime)
            }
        }
    }

    private fun groceryDaysChanged(preferenceKey: String?) =
        preferenceKey == SharedPreferencesHelper.GROCERY_DAYS_KEY

    private fun createPendingIntent(): PendingIntent {
        val notificationIntent = Intent(mContext, NotificationReceiver::class.java)
        return PendingIntent.getBroadcast(
            mContext,
            NOTIFICATION_ID,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun calculateTriggerTime() = SystemClock.elapsedRealtime() + mDaysToNotif

    private fun setAlarm(notificationPendingIntent: PendingIntent, triggerTime: Long) {
        mAlarmManager.setInexactRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            triggerTime,
            mDaysToNotif,
            notificationPendingIntent
        )
    }
}