package com.bsuuv.grocerymanager.data.db.entity

import androidx.room.TypeConverter
import com.bsuuv.grocerymanager.data.db.FoodItemDatabase
import com.bsuuv.grocerymanager.util.TimeFrame

/**
 * Helps [FoodItemDatabase] to convert the enum [TimeFrame] to integers that can be
 * persisted in the database.
 */
class TimeFrameConverter {
    companion object {
        @TypeConverter
        @JvmStatic
        fun toTimeFrame(timeFrame: Int): TimeFrame {
            return when (timeFrame) {
                TimeFrame.WEEK.value -> TimeFrame.WEEK
                TimeFrame.TWO_WEEKS.value -> TimeFrame.TWO_WEEKS
                TimeFrame.MONTH.value -> TimeFrame.MONTH
                else -> throw IllegalArgumentException("Integer $timeFrame couldn't be converted to a TimeFrame")
            }
        }

        @TypeConverter
        @JvmStatic
        fun toInteger(timeFrame: TimeFrame): Int = timeFrame.value
    }
}