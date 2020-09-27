package com.bsuuv.grocerymanager.util

import android.content.Context
import android.content.SharedPreferences
import com.bsuuv.grocerymanager.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject

/**
 * Utility class to help with date- and time- related operations.
 */
class DateTimeHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sharedPrefsHelper: SharedPreferencesHelper
) : SharedPreferences.OnSharedPreferenceChangeListener {

    private var groceryDays: MutableSet<String> = sharedPrefsHelper.getGroceryDays()
    private val calendar: Calendar
    internal var today: Int

    init {
        this.calendar = createCalendar()
        this.today = calendar.get(Calendar.DAY_OF_WEEK)
        this.sharedPrefsHelper.registerOnSharedPreferenceChangeListenter(this)
    }

    companion object {
        const val NO_GROCERY_DAYS_SET = 8
    }

    private fun createCalendar(): Calendar {
        val calendar: Calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.SUNDAY
        // When Date-object is instantiated without parameters, its time is set to the current day.
        calendar.time = Date()
        return calendar
    }

    /**
     * Returns a `Boolean` telling whether the user has set the current day as grocery day or not.
     */
    fun isGroceryDay(): Boolean {
        for (groceryDay in groceryDays) {
            val groceryDayInt = getOrdinalOfWeekday(groceryDay)
            if (groceryDayInt == today) return true
        }
        return false
    }

    fun getTimeUntilNextGroceryDay(): Int {
        var daysUntilClosestGroceryDay = NO_GROCERY_DAYS_SET
        for (groceryDay in groceryDays) {
            val groceryDayOrdinal = getNextGroceryDayOrdinal(groceryDay)
            val daysFromTodayToGroceryDay = groceryDayOrdinal - today
            if (daysFromTodayToGroceryDay < daysUntilClosestGroceryDay) {
                daysUntilClosestGroceryDay = daysFromTodayToGroceryDay
            }
        }
        return daysUntilClosestGroceryDay
    }

    private fun getNextGroceryDayOrdinal(groceryDay: String): Int {
        val groceryDayOrdinal = getOrdinalOfWeekday(groceryDay)
        return if (weekdayPassed(groceryDayOrdinal)) groceryDayOrdinal + 7 else groceryDayOrdinal
    }

    private fun getOrdinalOfWeekday(weekday: String): Int {
        val daysOfWeek = context.resources.getStringArray(R.array.daysofweek_datehelper)

        // Days of the week start from Sunday and are represented by integers 1..7
        return when (weekday) {
            daysOfWeek[0] -> 1
            daysOfWeek[1] -> 2
            daysOfWeek[2] -> 3
            daysOfWeek[3] -> 4
            daysOfWeek[4] -> 5
            daysOfWeek[5] -> 6
            daysOfWeek[6] -> 7
            else -> throw IllegalArgumentException("Given string $weekday didn't match any weekdays!")
        }
    }

    private fun weekdayPassed(weekdayOrdinal: Int): Boolean = weekdayOrdinal < today

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == SharedPreferencesHelper.GROCERY_DAYS_KEY) {
            groceryDays = sharedPrefsHelper.getGroceryDays()
        }
    }
}