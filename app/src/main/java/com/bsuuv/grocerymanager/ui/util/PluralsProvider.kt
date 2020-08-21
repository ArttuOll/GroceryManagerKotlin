package com.bsuuv.grocerymanager.ui.util

import android.content.Context
import com.bsuuv.grocerymanager.R
import com.bsuuv.grocerymanager.util.TimeFrame

class PluralsProvider(private val mContext: Context) {

    /**
     * Creates a string describing a food-item's schedule based on its frequency and time frame in
     * correct plural format. For example: when frequency = 1 and timeFrame =
     * <code>TimeFrame.WEEK</code>, the produced string is "Once a week". If frequency = 2, then the
     * string is "Twice in a week".
     *
     * @param frequency Frequency of the food-item which the string describes
     * @param timeFrame Time frame of the food-item which the string describes
     * @return String describing the schedule in which a food-item appears on the grocery list
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
     * Creates a string describing food-item's amount and unit in correct plural format. For example,
     * when amount = 1 and unit = piece, the resulting string is "One piece". If the amount = 2, then
     * the string is "Two pieces".
     *
     * @param amount Amount of the food-item which the string describes
     * @param unit   Unit of the food-item which the string describes
     * @return String describing the quantity of the food-item
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