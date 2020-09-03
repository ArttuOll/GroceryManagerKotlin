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
import org.junit.Before
import org.junit.Test

class GroceryListExtractorTest {

    private val mState: GroceryListState = mockk()
    private val mSharedPrefsHelper: SharedPreferencesHelper = mockk()
    private val mModifiedList: MutableList<FoodItem> = mockk()
    private val mRemovedItems: MutableList<FoodItem> = mockk()

    private val mGroceryListExtractor = GroceryListExtractor(mState, mSharedPrefsHelper)

    private lateinit var mFoodItems: MutableList<FoodItemEntity>
    private lateinit var mFoodItem1: FoodItemEntity
    private lateinit var mFoodItem2: FoodItemEntity
    private lateinit var mModifiedFoodItem: FoodItemEntity
    private lateinit var mCheckedFoodItem: FoodItemEntity
    private lateinit var mModifiedCheckedFoodItem: FoodItemEntity

    @Before
    fun init() {
        clearAllMocks()
        initMembers()
        configureMocks()
    }

    private fun initMembers() {
        mFoodItems = ArrayList()
        mFoodItem1 = FoodItemEntity(
            0, "",
            "Kalja", "Karjala", "Raikasta",
            2, "Packets", TimeFrame.TWO_WEEKS, 1, 0.0
        )
        mFoodItem2 = FoodItemEntity(
            0, "",
            "Makkara", "Atria", "Lihaisaa",
            3, "Bags", TimeFrame.MONTH, 1, 0.0
        )
        mModifiedFoodItem = FoodItemEntity(
            0, "",
            "Parsakaali", "", "Tylsää",
            5, "Bags", TimeFrame.WEEK, 1, 0.0
        )
        mCheckedFoodItem = FoodItemEntity(
            0, "",
            "Kanaa", "Saarioinen",
            "Tylsää",
            5, "Bags", TimeFrame.WEEK, 1, 0.0
        )
        mModifiedCheckedFoodItem = FoodItemEntity(
            0, "",
            "Voi", "Valio",
            "Tylsää",
            5, "Bags", TimeFrame.WEEK, 1, 0.0
        )
    }

    private fun configureMocks() {
        every { mState.removedItems } returns mRemovedItems
        every { mRemovedItems.contains(mCheckedFoodItem) } returns true
        every { mRemovedItems.contains(mModifiedFoodItem) } returns true
        every { mSharedPrefsHelper.getGroceryDays() } returns HashSet()
        every { mState.incrementedItems } returns ArrayList()
    }

    @After
    fun clean() {
        mFoodItems.clear()
        mFoodItem1.countdownValue = 0.0
        mFoodItem2.countdownValue = 0.0
        mModifiedFoodItem.countdownValue = 0.0
        mCheckedFoodItem.countdownValue = 0.0
        mModifiedCheckedFoodItem.countdownValue = 0.0
    }

    @Test
    fun getGroceryList_noFoodItemsReadyToAppear() {
        mFoodItems.add(mFoodItem1)
        mFoodItems.add(mFoodItem2)

        val actual = mGroceryListExtractor.extractGroceryListFromFoodItems(mFoodItems)
        val expected = ArrayList<FoodItemEntity>()

        verify { mState.increment(mFoodItem1) }
        verify { mState.increment(mFoodItem2) }
    }
}