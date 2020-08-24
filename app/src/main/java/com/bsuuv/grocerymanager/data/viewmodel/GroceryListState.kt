package com.bsuuv.grocerymanager.data.viewmodel

import com.bsuuv.grocerymanager.data.model.FoodItem
import com.bsuuv.grocerymanager.util.SharedPreferencesHelper

class GroceryListState(private val mSharedPrefsHelper: SharedPreferencesHelper) {

    companion object {
        const val INCREMENTED_ITEMS_KEY = "incrementedItems"
        const val REMOVED_ITEMS_KEY = "removedItems"
    }

    private val mIncrementedItems: MutableList<FoodItem>
    private val mRemovedItems: MutableList<FoodItem>

    init {
        mIncrementedItems = mSharedPrefsHelper.getList(INCREMENTED_ITEMS_KEY)
        mRemovedItems = mSharedPrefsHelper.getList(REMOVED_ITEMS_KEY)
    }

    fun remove(foodItem: FoodItem) = mRemovedItems.add(foodItem)

    fun increment(foodItem: FoodItem) {
        if (notIncremented(foodItem)) mIncrementedItems.add(foodItem)
    }

    private fun notIncremented(foodItem: FoodItem): Boolean = !mIncrementedItems.contains(foodItem)

    fun saveState() {
        mSharedPrefsHelper.saveList(mIncrementedItems, INCREMENTED_ITEMS_KEY)
        mSharedPrefsHelper.saveList(mRemovedItems, REMOVED_ITEMS_KEY)
    }

    fun resetState() {
        mSharedPrefsHelper.clearList(INCREMENTED_ITEMS_KEY)
        mSharedPrefsHelper.clearList(REMOVED_ITEMS_KEY)
    }
}