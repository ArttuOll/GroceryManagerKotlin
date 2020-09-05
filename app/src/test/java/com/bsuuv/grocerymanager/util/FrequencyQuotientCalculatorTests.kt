package com.bsuuv.grocerymanager.util

import com.bsuuv.grocerymanager.data.db.entity.FoodItemEntity
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class FrequencyQuotientCalculatorTests {

    private val mSharedPrefsHelper: SharedPreferencesHelper = mockk()
    private val mGroceryDays = HashSet<String>()

    @Before
    fun init() {
        every { mSharedPrefsHelper.getGroceryDays() } returns mGroceryDays
    }

    @Test
    fun singleGroceryDay() {
        mGroceryDays.add("Tuesday")
        val foodItem = FoodItemEntity(
            0, "", "Olut", "Karjala",
            "Raikasta", 2, "Packets", TimeFrame.TWO_WEEKS, 1, 0.0
        )
        Assert.assertEquals(
            0.5, FrequencyQuotientCalc.calculate(mSharedPrefsHelper, foodItem),
            .001
        )
        Assert.assertEquals(
            0.5, FrequencyQuotientCalc.calculate(1, TimeFrame.TWO_WEEKS, 1),
            .001
        )
    }

    @Test
    fun multipleGroceryDays() {
        mGroceryDays.add("Tuesday")
        mGroceryDays.add("Wednesday")
        mGroceryDays.add("Saturday")
        val foodItem = FoodItemEntity(
            0, "", "Olut", "Karjala",
            "Raikasta", 2, "Packets", TimeFrame.TWO_WEEKS, 1, 0.0
        )
        Assert.assertEquals(
            0.15, FrequencyQuotientCalc.calculate(mSharedPrefsHelper, foodItem),
            .001
        )
        Assert.assertEquals(
            0.15, FrequencyQuotientCalc.calculate(1, TimeFrame.TWO_WEEKS, 3),
            .001
        )
    }
}