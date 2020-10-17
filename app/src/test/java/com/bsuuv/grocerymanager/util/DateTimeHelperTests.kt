package com.bsuuv.grocerymanager.util

import android.content.Context
import android.content.res.Resources
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class DateTimeHelperTests {

    private val mContext: Context = mockk()
    private val mResources: Resources = mockk()
    private val mSharedPrefsHelper: SharedPreferencesHelper = mockk()
    private val mGroceryDays: HashSet<String> = spyk()
    private lateinit var mDateTimeHelper: DateTimeHelper
    private val daysOfWeek = arrayOf(
        "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday",
        "Friday", "Saturday"
    )

    @Before
    fun init() {
        every { mContext.resources } returns mResources
        every { mResources.getStringArray(any()) } returns daysOfWeek
        every { mSharedPrefsHelper.getGroceryDays() } returns mGroceryDays
        every { mSharedPrefsHelper.registerOnSharedPreferenceChangeListener(any()) } returns Unit
        mDateTimeHelper = DateTimeHelper(mContext, mSharedPrefsHelper)
    }

    @After
    fun clear() {
        clearAllMocks()
        mGroceryDays.clear()
    }

    @Test
    fun isGroceryDay() {
        mDateTimeHelper.today = 2
        mGroceryDays.add("Monday")
        Assert.assertTrue(mDateTimeHelper.isGroceryDay())
    }

    @Test
    fun isGroceryDay_edgeCase1() {
        mDateTimeHelper.today = 1
        mGroceryDays.add("Sunday")
        Assert.assertTrue(mDateTimeHelper.isGroceryDay())
    }

    @Test
    fun isGroceryDay_edgeCase2() {
        mDateTimeHelper.today = 7
        mGroceryDays.add("Saturday")
        Assert.assertTrue(mDateTimeHelper.isGroceryDay())
    }

    @Test
    fun timeUntilNextGroceryDay_groceryWeekdayInFuture() {
        mDateTimeHelper.today = 2
        mGroceryDays.add("Tuesday")
        Assert.assertEquals(1, mDateTimeHelper.getTimeUntilNextGroceryDay())
    }

    @Test
    fun timeUntilNextGroceryDay_groceryWeekdayInPast() {
        mDateTimeHelper.today = 4
        mGroceryDays.add("Monday")
        Assert.assertEquals(5, mDateTimeHelper.getTimeUntilNextGroceryDay())
    }
}