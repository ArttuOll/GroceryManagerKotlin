package com.bsuuv.grocerymanager.util

import com.bsuuv.grocerymanager.data.model.FoodItem
import kotlin.math.round

/**
 * Utility class for calculating food-item frequency quotients.
 *
 * Frequency quotient is food-item's frequency divided by multiplication of its time frame and the
 * amount of grocery days in a week. It is used as an unit of incrementation for a food-item's
 * countdown value.
 *
 * A food-item appears on the grocery list when its countdown value is 1.0 (or greater). The
 * countdown value starts at the value of the food-item's frequency quotient and each grocery day
 * the countdown value is increased by the amount of the food-item's frequency quotient. The
 * frequency quotient makes sure the food-item's countdown value reaches 1.0 exactly when the user
 * wants it (based on the time frame and frequency properties set by the user).
 *
 * Example: The user wants to have a certain food-item appear on the grocery list once a week. He
 * has set there to be two grocery days in a week. When creating the food-item, he chooses a
 * frequency of 1 and a time frame of a week (remember, TimeFrame.WEEk = 1). Thus the frequency
 * quotient is: 1 / (1 * 2) = 0.5. On the next grocery day the countdown value is 0.5, so the item
 * is not shown on the grocery list. On the next grocery day after that, the countdown value is
 * summed by the frequency quotient, so its value becomes 1.0 and it's shown on the grocery list.
 *
 * The countdown value can exceed 1.0 if the frequency quotient changes between grocery days.
 */
class FrequencyQuotientCalc {
    companion object {
        /**
         * Uses the given [sharedPrefsHelper] to calculate and return the frequency quotient
         * of the given [foodItem], rounded to the nearest 0.05.
         */
        fun calculate(sharedPrefsHelper: SharedPreferencesHelper, foodItem: FoodItem): Double {
            val groceryDaysAWeek = sharedPrefsHelper.getGroceryDays().size
            val frequency = foodItem.frequency.toDouble()
            val timeFrame = foodItem.timeFrame.value
            val frequencyQuotient = frequency / (timeFrame * groceryDaysAWeek)
            return round(frequencyQuotient * 20) / 20.0
        }

        /**
         * Based on the given [frequency], [timeFrame] and [groceryDaysAWeek] values, calculates
         * and returns a frequency quotient, rounded to the nearest 0.05.
         * @param frequency        Frequency of the food-item
         * @param timeFrame        Time frame of the food-item
         * @param groceryDaysAWeek Number of grocery days a week
         * @return Frequency quotient calculated based on the given arguments, rounded to the nearest
         * 0.05.
         */
        fun calculate(frequency: Int, timeFrame: TimeFrame, groceryDaysAWeek: Int): Double {
            val timeFrameValue = timeFrame.value
            val frequencyQuotient = frequency.toDouble() / (timeFrameValue * groceryDaysAWeek)
            return round(frequencyQuotient * 20) / 20.0
        }
    }
}