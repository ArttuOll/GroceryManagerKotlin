package com.bsuuv.grocerymanager.util

import android.content.Context
import com.bsuuv.grocerymanager.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility class to help with date- and time- related operations.
 */
class DateTimeHelper(private val mContext: Context, sharedPrefsHelper: SharedPreferencesHelper) {

    private val mGroceryDays: MutableSet<String> = sharedPrefsHelper.getGroceryDays()
    private val mCalendar: Calendar
    private var mToday: Int

    init {
        this.mCalendar = createCalendar()
        this.mToday = mCalendar.get(Calendar.DAY_OF_WEEK)
    }

    private fun createCalendar(): Calendar {
        val calendar: Calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.SUNDAY
        // When Date-object is instantiated without parameters, its time is set to the current day.
        calendar.time = Date()
        return calendar
    }

    /**
     * @return <code>Boolean</code> telling whether the current day is set as
     * grocery day by the user or not
     */
    fun isGroceryDay(): Boolean {
        for (groceryDay in mGroceryDays) {
            val groceryDayInt = getOrdinalOfWeekday(groceryDay)
            if (groceryDayInt == mToday) return true
        }
        return false
    }

    fun getTimeUntilNextGroceryDay(): Int {
        var daysUntilClosestGroceryDay = Keys.NO_GROCERY_DAYS_SET
        for (groceryDay in mGroceryDays) {
            val groceryDayOrdinal = getNextGroceryDayOrdinal(groceryDay)
            val daysFromTodayToGroceryDay = groceryDayOrdinal - mToday
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
        val daysOfWeek = mContext.resources.getStringArray(R.array.daysofweek_datehelper)

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

    private fun weekdayPassed(weekdayOrdinal: Int): Boolean = weekdayOrdinal < mToday

    fun getCurrentDate(): String {
        val defaultLocaleFormat = SimpleDateFormat.getDateInstance()
        return defaultLocaleFormat.format(mCalendar.time)
    }

    internal fun setToday(today: Int) {
        mToday = today
    }

    object Keys {
        const val NO_GROCERY_DAYS_SET = 8
    }
}