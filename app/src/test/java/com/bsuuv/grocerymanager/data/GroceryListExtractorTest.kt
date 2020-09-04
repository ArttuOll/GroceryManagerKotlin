package com.bsuuv.grocerymanager.data

import com.bsuuv.grocerymanager.data.db.entity.FoodItemEntity
import com.bsuuv.grocerymanager.data.model.FoodItem
import com.bsuuv.grocerymanager.util.SharedPreferencesHelper
import com.bsuuv.grocerymanager.util.TimeFrame
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class GroceryListExtractorTest {

    private val mState: GroceryListState = mockk(relaxed = true)
    private val mSharedPrefsHelper: SharedPreferencesHelper = mockk()
    private val mIncrementedList: MutableList<FoodItem> = mockk()
    private var mGroceryListExtractor = GroceryListExtractor(mState, mSharedPrefsHelper)

    private lateinit var mFoodItems: MutableList<FoodItemEntity>
    private lateinit var mFoodItem1: FoodItemEntity
    private lateinit var mFoodItem2: FoodItemEntity

    @Before
    fun init() {
        initMembers()
        every { mSharedPrefsHelper.getGroceryDays().size } returns 1
    }

    private fun initMembers() {
        mFoodItems = ArrayList()
        mFoodItem1 = FoodItemEntity(
            0, "",
            "Kalja", "Karjala", "Raikasta",
            2, "Packets", TimeFrame.WEEK, 1, 0.0
        )
        mFoodItem2 = FoodItemEntity(
            0, "",
            "Makkara", "Atria", "Lihaisaa",
            3, "Bags", TimeFrame.WEEK, 1, 0.0
        )
    }

    @After
    fun clean() {
        clearAllMocks()
        mFoodItems.clear()
        mFoodItem1.countdownValue = 0.0
        mFoodItem2.countdownValue = 0.0
    }

    @Test
    fun noFoodItemsAddedWhenNoneReadyToAppear() {
        mFoodItems.add(mFoodItem1)
        mFoodItems.add(mFoodItem2)
        assertProducesEmptyGroceryList()
        verifyFoodItemsIncremented(mFoodItem1, mFoodItem2)
    }

    @Test
    fun foodItemsAddedWhenReadyToAppear() {
        addItemsToFoodItemsAsReadyToAppear(mFoodItem1, mFoodItem2)
        every { mState.removedItems.contains(any()) } returns false
        assertProducesGroceryListOf(mFoodItem1, mFoodItem2)
        verifyFoodItemsIncremented(mFoodItem1, mFoodItem2)
    }

    private fun verifyFoodItemsIncremented(vararg foodItems: FoodItemEntity) {
        for (foodItem in foodItems) {
            verify { mState.increment(foodItem) }
        }
    }

    @Test
    fun foodItemNotIncrementedIfIncrementedAlready() {
        addItemsToFoodItemsAsReadyToAppear(mFoodItem1)
        every { mState.incrementedItems.contains(mFoodItem1) } returns true
        assertProducesGroceryListOf(mFoodItem1)
        verify(exactly = 0) { mIncrementedList.add(mFoodItem1) }
    }

    private fun assertProducesGroceryListOf(vararg foodItems: FoodItemEntity) {
        val actual = mGroceryListExtractor.extractGroceryListFromFoodItems(mFoodItems)
        val expected = ArrayList<FoodItemEntity>()
        expected.addAll(foodItems)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun foodItemNotAddedIfRemoved() {
        addItemsToFoodItemsAsReadyToAppear(mFoodItem1)
        every { mState.removedItems.contains(mFoodItem1) } returns true
        assertProducesEmptyGroceryList()
    }

    private fun assertProducesEmptyGroceryList() {
        val actual = mGroceryListExtractor.extractGroceryListFromFoodItems(mFoodItems)
        val expected = ArrayList<FoodItemEntity>()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun foodItemNotAddedIfIncrementedAndModified() {
        addItemsToFoodItemsAsReadyToAppear(mFoodItem1)
        every { mState.removedItems.contains(mFoodItem1) } returns true
        every { mState.incrementedItems.contains(mFoodItem1) } returns true
        assertProducesEmptyGroceryList()
    }

    private fun addItemsToFoodItemsAsReadyToAppear(vararg foodItems: FoodItemEntity) {
        for (foodItem in foodItems) {
            foodItem.countdownValue = 1.0
            mFoodItems.add(foodItem)
        }
    }
}