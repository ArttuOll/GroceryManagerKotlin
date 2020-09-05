package com.bsuuv.grocerymanager.data

import com.bsuuv.grocerymanager.data.db.entity.FoodItemEntity
import com.bsuuv.grocerymanager.data.model.FoodItem
import com.bsuuv.grocerymanager.util.SharedPreferencesHelper

class GroceryListState(private val mSharedPrefsHelper: SharedPreferencesHelper) {

    companion object {
        const val INCREMENTED_ITEMS_KEY = "incrementedItems"
        const val REMOVED_ITEMS_KEY = "removedItems"
    }

    val incrementedItems: MutableList<FoodItemEntity>
    internal val removedItems: MutableList<FoodItemEntity>

    init {
        incrementedItems = mSharedPrefsHelper.getList(INCREMENTED_ITEMS_KEY)
        removedItems = mSharedPrefsHelper.getList(REMOVED_ITEMS_KEY)
    }

    fun remove(foodItem: FoodItem) = removedItems.add(foodItem as FoodItemEntity)

    fun increment(foodItem: FoodItem) {
        if (notIncremented(foodItem)) incrementedItems.add(foodItem as FoodItemEntity)
    }

    private fun notIncremented(foodItem: FoodItem): Boolean = !incrementedItems.contains(foodItem)

    fun save() {
        mSharedPrefsHelper.saveList(incrementedItems, INCREMENTED_ITEMS_KEY)
        mSharedPrefsHelper.saveList(removedItems, REMOVED_ITEMS_KEY)
    }

    fun reset() {
        mSharedPrefsHelper.clearList(INCREMENTED_ITEMS_KEY)
        mSharedPrefsHelper.clearList(REMOVED_ITEMS_KEY)
    }
}