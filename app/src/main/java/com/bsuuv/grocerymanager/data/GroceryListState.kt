package com.bsuuv.grocerymanager.data

import com.bsuuv.grocerymanager.data.db.entity.FoodItemEntity
import com.bsuuv.grocerymanager.data.model.FoodItem
import com.bsuuv.grocerymanager.util.SharedPreferencesHelper

/**
 * Represent the state of the grocery list on any given moment. The state consists of food-items
 * that were not displayed on the grocery list yet, but whose countdown values (for information on
 * countdown values, see [GroceryListExtractor]) were incremented, and food-items that the user
 * has removed from the grocery list.
 */
class GroceryListState(private val sharedPrefsHelper: SharedPreferencesHelper) {

    companion object {
        const val INCREMENTED_ITEMS_KEY = "incrementedItems"
        const val REMOVED_ITEMS_KEY = "removedItems"
    }

    val incrementedItems: MutableList<FoodItemEntity>
    internal val removedItems: MutableList<FoodItemEntity>

    init {
        incrementedItems = sharedPrefsHelper.getList(INCREMENTED_ITEMS_KEY)
        removedItems = sharedPrefsHelper.getList(REMOVED_ITEMS_KEY)
    }

    fun remove(foodItem: FoodItem) = removedItems.add(foodItem as FoodItemEntity)

    fun increment(foodItem: FoodItem) {
        if (notIncremented(foodItem)) {
            incrementedItems.add(foodItem as FoodItemEntity)
        }
    }

    private fun notIncremented(foodItem: FoodItem): Boolean = !incrementedItems.contains(foodItem)

    fun save() {
        sharedPrefsHelper.saveList(incrementedItems, INCREMENTED_ITEMS_KEY)
        sharedPrefsHelper.saveList(removedItems, REMOVED_ITEMS_KEY)
    }

    fun reset() {
        sharedPrefsHelper.clearList(INCREMENTED_ITEMS_KEY)
        sharedPrefsHelper.clearList(REMOVED_ITEMS_KEY)
    }
}