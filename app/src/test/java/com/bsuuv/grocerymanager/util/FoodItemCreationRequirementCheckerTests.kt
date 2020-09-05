package com.bsuuv.grocerymanager.util

import com.bsuuv.grocerymanager.R
import com.bsuuv.grocerymanager.ui.util.FoodItemCreationRequirementChecker
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class FoodItemCreationRequirementCheckerTests {

    private lateinit var mGroceryDays: HashSet<String>
    private lateinit var mTextFieldValues: MutableList<String>
    private lateinit var mChecker: FoodItemCreationRequirementChecker
    private var mAmount = 1
    private var mTimeFrame = TimeFrame.WEEK
    private var mFrequency = 1
    private var mFrequencyQuotient = 1.0
    private val mSharedPreferencesHelper: SharedPreferencesHelper = mockk()

    @Before
    fun init() {
        mChecker = FoodItemCreationRequirementChecker(mSharedPreferencesHelper)
        mGroceryDays = hashSetOf("Monday")
        mTextFieldValues = mutableListOf("testi")
        every { mSharedPreferencesHelper.getGroceryDays() } returns mGroceryDays
    }

    @Test
    fun allRequirementsMet() {
        Assert.assertTrue(
            mChecker.requirementsMet(
                mTextFieldValues,
                mAmount,
                mTimeFrame,
                mFrequency,
                mFrequencyQuotient
            )
        )
    }

    @Test
    fun emptyLabel() {
        mTextFieldValues.clear()
        mTextFieldValues.add("")
        try {
            mChecker.requirementsMet(
                mTextFieldValues,
                mAmount,
                mTimeFrame,
                mFrequency,
                mFrequencyQuotient
            )
        } catch (e: FoodItemCreationRequirementChecker.RequirementNotMetException) {
            Assert.assertEquals(R.string.snackbar_label_empty, e.messageResId)
        }
    }

    @Test
    fun amountZero() {
        mAmount = 0
        try {
            mChecker.requirementsMet(
                mTextFieldValues,
                mAmount,
                mTimeFrame,
                mFrequency,
                mFrequencyQuotient
            )
        } catch (e: FoodItemCreationRequirementChecker.RequirementNotMetException) {
            Assert.assertEquals(R.string.snackbar_amount_empty, e.messageResId)
        }
    }

    @Test
    fun timeFrameNull() {
        mTimeFrame = TimeFrame.NULL
        try {
            mChecker.requirementsMet(
                mTextFieldValues,
                mAmount,
                mTimeFrame,
                mFrequency,
                mFrequencyQuotient
            )
        } catch (e: FoodItemCreationRequirementChecker.RequirementNotMetException) {
            Assert.assertEquals(R.string.snackbar_time_frame_not_chosen, e.messageResId)
        }
    }

    @Test
    fun frequencyZero() {
        mFrequency = 0
        try {
            mChecker.requirementsMet(
                mTextFieldValues,
                mAmount,
                mTimeFrame,
                mFrequency,
                mFrequencyQuotient
            )
        } catch (e: FoodItemCreationRequirementChecker.RequirementNotMetException) {
            Assert.assertEquals(R.string.snackbar_frequency_not_set, e.messageResId)
        }
    }

    @Test
    fun frequencyQuotientTooLarge() {
        mFrequencyQuotient = 1.5
        try {
            mChecker.requirementsMet(
                mTextFieldValues,
                mAmount,
                mTimeFrame,
                mFrequency,
                mFrequencyQuotient
            )
        } catch (e: FoodItemCreationRequirementChecker.RequirementNotMetException) {
            Assert.assertEquals(R.string.snackbar_not_enough_grocery_days, e.messageResId)
        }
    }
}