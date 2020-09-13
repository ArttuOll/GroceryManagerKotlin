package com.bsuuv.grocerymanager.data

import com.bsuuv.grocerymanager.data.db.entity.FoodItemEntity
import com.bsuuv.grocerymanager.util.FrequencyQuotientCalc
import com.bsuuv.grocerymanager.util.SharedPreferencesHelper

/**
 * Business logic class that coordinates when food-items are ready to appear in the grocery list.
 *
 * Food-items are ready to appear in the grocery list, when their countdown value is > 1.0 and they
 * haven't been removed from the grocery list by the user. Food-item's countdown value starts at its
 * frequency quotient (see [FrequencyQuotientCalc] for more details on frequency
 * quotients) and is incremented by the value of its frequency quotient whenever its eligibility to
 * be on the grocery list is evaluated. When a food-item is added on the grocery list, its countdown
 * value is reset to its frequency quotient.
 */
class GroceryListExtractor(
    private val mGroceryListState: GroceryListState,
    private val mSharedPrefsHelper: SharedPreferencesHelper
) {

    /**
     * Iterates through the given list of food-items and checks which of them are ready to appear on
     * the grocery list. A food-item is ready to appear on the grocery list, if its countdown
     * value is > 1.0 and it hasn't been previously removed from it.
     */
    fun extractGroceryListFromFoodItems(foodItems: MutableList<FoodItemEntity>)
            : MutableList<FoodItemEntity> {
        val groceries = ArrayList<FoodItemEntity>()
        for (foodItem in foodItems) {
            addEligibleItemsToGroceryList(groceries, foodItem)
        }
        return groceries
    }

    private fun addEligibleItemsToGroceryList(
        groceries: ArrayList<FoodItemEntity>,
        foodItem: FoodItemEntity
    ) {
        val frequencyQuotient = FrequencyQuotientCalc.calculate(mSharedPrefsHelper, foodItem)
        if (shouldAppearInGroceryList(foodItem)) {
            groceries.add(foodItem)
            resetCountdownValue(foodItem, frequencyQuotient)
        } else {
            incrementCountdownValue(foodItem, frequencyQuotient)
        }
        mGroceryListState.increment(foodItem)
    }

    private fun shouldAppearInGroceryList(foodItem: FoodItemEntity): Boolean =
        foodItem.countdownValue >= 1 && !mGroceryListState.removedItems.contains(foodItem)

    private fun resetCountdownValue(foodItem: FoodItemEntity, frequencyQuotient: Double) {
        foodItem.countdownValue = frequencyQuotient
    }

    private fun incrementCountdownValue(foodItem: FoodItemEntity, frequencyQuotient: Double) {
        foodItem.countdownValue = foodItem.countdownValue + frequencyQuotient
    }
}
