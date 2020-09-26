package com.bsuuv.grocerymanager.ui.util

import com.bsuuv.grocerymanager.R
import com.bsuuv.grocerymanager.ui.NewFoodItemActivity
import com.bsuuv.grocerymanager.util.FrequencyQuotientCalc
import com.bsuuv.grocerymanager.util.SharedPreferencesHelper
import com.bsuuv.grocerymanager.util.TimeFrame

/**
 * Utility class containing logic to check that given food-item properties fit their requirements.
 * This class is used in [NewFoodItemActivity] to validate the values in its input fields.
 */
class FoodItemCreationRequirementChecker(private val mSharedPrefsHelper: SharedPreferencesHelper) {

    companion object {
        const val FREQUENCY_NOT_SET = 0
        const val AMOUNT_FIELD_EMPTY = 0
        const val MAX_FREQUENCY_QUOTIENT = 1.0
    }

    /**
     * Returns a boolean telling if all of the requirements were met.
     *
     * The requirements are as follows:
     *
     *   * Label must not be empty
     *   * Amount must be greater than zero
     *   * TimeFrame must not equal `TimeFrame.NULL`
     *   * Frequency must be greater than zero
     *   * Frequency quotient must not be more than 1.0 [FrequencyQuotientCalc]).
     * @throws RequirementNotMetException An exception with message telling which of the requirements
     *                                    was not met.
     */
    fun requirementsMet(
        textFieldValues: MutableMap<String, String>, amount: Int, timeFrame: TimeFrame, frequency:
        Int,
        frequencyQuotient: Double
    ): Boolean {
        return groceryDaysSet() &&
                inputFieldsValid(textFieldValues, amount, timeFrame, frequency) &&
                frequencyQuotientValid(frequencyQuotient)
    }

    private fun groceryDaysSet(): Boolean {
        val groceryDaysAWeek = mSharedPrefsHelper.getGroceryDays().size
        if (groceryDaysAWeek > 0) return true
        else throw RequirementNotMetException(R.string.snackbar_no_grocery_days)
    }

    private fun inputFieldsValid(
        textFieldValues: MutableMap<String, String>,
        amount: Int,
        timeFrame: TimeFrame,
        frequency: Int
    ): Boolean {
        return labelFieldValid(textFieldValues) &&
                amountFieldValid(amount) &&
                timeFrameSelected(timeFrame) &&
                frequencyFieldSet(frequency)
    }

    private fun labelFieldValid(textFieldValues: MutableMap<String, String>): Boolean {
        val label = textFieldValues["label"]
        val labelNotEmpty = label?.isNotEmpty() ?: false
        if (labelNotEmpty) return true
        else throw RequirementNotMetException(R.string.snackbar_label_empty)
    }

    private fun amountFieldValid(amount: Int): Boolean {
        if (amount > AMOUNT_FIELD_EMPTY) return true
        else throw RequirementNotMetException(R.string.snackbar_amount_empty)
    }

    private fun timeFrameSelected(timeFrame: TimeFrame): Boolean {
        if (timeFrame != TimeFrame.NULL) return true
        else throw RequirementNotMetException(R.string.snackbar_time_frame_not_chosen)
    }

    private fun frequencyFieldSet(frequency: Int): Boolean {
        if (frequency > FREQUENCY_NOT_SET) return true
        else throw RequirementNotMetException(R.string.snackbar_frequency_not_set)
    }

    private fun frequencyQuotientValid(frequencyQuotient: Double): Boolean {
        if (frequencyQuotient <= MAX_FREQUENCY_QUOTIENT) return true
        else throw RequirementNotMetException(R.string.snackbar_not_enough_grocery_days)
    }

    class RequirementNotMetException(val messageResId: Int) : Exception()
}

