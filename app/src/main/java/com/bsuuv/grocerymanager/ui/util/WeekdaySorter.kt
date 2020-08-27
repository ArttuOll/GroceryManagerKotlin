package com.bsuuv.grocerymanager.ui.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Utility for sorting weekdays. This class uses Monday as the first day of the week.
 */
class WeekdaySorter {

    class WeekdayComparator : Comparator<String> {
        override fun compare(weekday1: String, weekday2: String): Int {
            val day1 = convertWeekdayToOrdinal(weekday1)
            val day2 = convertWeekdayToOrdinal(weekday2)
            return day1 - day2
        }

        @SuppressLint("SimpleDateFormat")
        private fun convertWeekdayToOrdinal(weekday: String): Int {
            val format = SimpleDateFormat("E")
            val weekdayDate = format.parse(weekday)!!
            return convertDateToInt(weekdayDate)
        }

        private fun convertDateToInt(date: Date): Int {
            val calendar = createCalendar(date)
            val weekdayOrdinal = calendar.get(Calendar.DAY_OF_WEEK)
            // Make Sunday the last day of the week by returning 8 instead of 1
            return if (weekdayOrdinal == Calendar.SUNDAY) 8 else weekdayOrdinal
        }

        private fun createCalendar(date: Date): Calendar {
            val cal = Calendar.getInstance()
            cal.firstDayOfWeek = Calendar.MONDAY
            cal.time = date
            return cal
        }
    }

    companion object {
        /**
         * Sorts a set of weekdays, using Monday as the beginning of the week. Since <code>Set</code>
         * doesn't hold any order of its items, this method turns the set into a <code>List</code>.
         *
         * @param unsortedWeekdays Set of weekdays to sort
         * @return List of sorted weekdays
         */
        fun getAsSortedList(weekdays: MutableSet<String>): MutableList<String> {
            val weekdayList = ArrayList(weekdays)
            Collections.sort(weekdayList, WeekdayComparator())
            return weekdayList
        }
    }
}