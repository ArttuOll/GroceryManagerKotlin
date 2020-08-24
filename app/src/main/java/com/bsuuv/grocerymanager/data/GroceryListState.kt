package com.bsuuv.grocerymanager.data

import com.bsuuv.grocerymanager.data.model.FoodItem
import com.bsuuv.grocerymanager.util.SharedPreferencesHelper

class GroceryListState(private val mSharedPrefsHelper: SharedPreferencesHelper) {

    companion object {
        const val INCREMENTED_ITEMS_KEY = "incrementedItems"
        const val REMOVED_ITEMS_KEY = "removedItems"
    }

    val incrementedItems: MutableList<FoodItem>
    internal val removedItems: MutableList<FoodItem>

    init {
        incrementedItems = mSharedPrefsHelper.getList(INCREMENTED_ITEMS_KEY)
        removedItems = mSharedPrefsHelper.getList(REMOVED_ITEMS_KEY)
    }

    fun remove(foodItem: FoodItem) = removedItems.add(foodItem)

    fun increment(foodItem: FoodItem) {
        if (notIncremented(foodItem)) incrementedItems.add(foodItem)
    }

    private fun notIncremented(foodItem: FoodItem): Boolean = !incrementedItems.contains(foodItem)

    fun saveState() {
        mSharedPrefsHelper.saveList(incrementedItems, INCREMENTED_ITEMS_KEY)
        mSharedPrefsHelper.saveList(removedItems, REMOVED_ITEMS_KEY)
    }

    fun resetState() {
        mSharedPrefsHelper.clearList(INCREMENTED_ITEMS_KEY)
        mSharedPrefsHelper.clearList(REMOVED_ITEMS_KEY)
    }
}