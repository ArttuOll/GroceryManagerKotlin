package com.bsuuv.grocerymanager.data.db.entity

import androidx.room.TypeConverter
import com.bsuuv.grocerymanager.util.TimeFrame

/**
 * Helps {@link FoodItemRoomDatabase} to convert the enum {@link TimeFrame} to integers that can be
 * persisted in the database.
 */
class TimeFrameConverter {
    companion object {
        @TypeConverter
        fun toTimeFrame(timeFrame: Int): TimeFrame {
            return when (timeFrame) {
                TimeFrame.WEEK.value -> TimeFrame.WEEK
                TimeFrame.TWO_WEEKS.value -> TimeFrame.TWO_WEEKS
                TimeFrame.MONTH.value -> TimeFrame.MONTH
                else -> throw IllegalArgumentException("Integer $timeFrame couldn't be converted to a TimeFrame")
            }
        }

        @TypeConverter
        fun toInteger(timeFrame: TimeFrame): Int = timeFrame.value
    }
}