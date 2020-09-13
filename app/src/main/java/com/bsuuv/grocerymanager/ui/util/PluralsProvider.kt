package com.bsuuv.grocerymanager.ui.util

import android.content.Context
import com.bsuuv.grocerymanager.R
import com.bsuuv.grocerymanager.util.TimeFrame
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PluralsProvider @Inject constructor(@ApplicationContext private val mContext: Context) {

    /**
     * Returns a string describing a food-item's schedule based on its [frequency] and [timeFrame] in
     * correct plural format. For example: when [frequency] = 1 and [timeFrame] =
     * `TimeFrame.WEEK`, the produced string is "Once a week". If frequency = 2, then the
     * string is "Twice in a week".
     */
    fun getScheduleString(frequency: Int, timeFrame: TimeFrame): String {
        val resources = mContext.resources
        return when (timeFrame) {
            TimeFrame.WEEK -> {
                resources.getQuantityString(R.plurals.times_a_week, frequency, frequency)
            }
            TimeFrame.TWO_WEEKS -> {
                resources.getQuantityString(R.plurals.times_in_two_weeks, frequency, frequency)
            }
            TimeFrame.MONTH -> {
                resources.getQuantityString(R.plurals.times_in_a_month, frequency, frequency)
            }
            else -> ""
        }
    }

    /**
     * Returns a string describing food-item's [amount] and [unit] in correct plural format. For example,
     * when amount = 1 and unit = piece, the resulting string is "One piece". If the amount = 2, then
     * the string is "Two pieces".
     */
    fun getAmountString(amount: Int, unit: String): String {
        val resources = mContext.resources
        val units = resources.getStringArray(R.array.units_plural)
        when (unit) {
            units[0] -> resources.getQuantityString(R.plurals.Pieces, amount, amount)
            units[1] -> resources.getQuantityString(R.plurals.Packets, amount, amount)
            units[2] -> resources.getQuantityString(R.plurals.Cans, amount, amount)
            units[3] -> resources.getQuantityString(R.plurals.Bags, amount, amount)
            units[4] -> resources.getQuantityString(R.plurals.Bottles, amount, amount)
        }
        return amount.toString()
    }
}