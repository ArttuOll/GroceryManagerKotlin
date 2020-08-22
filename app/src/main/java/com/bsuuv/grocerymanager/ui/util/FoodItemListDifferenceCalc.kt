package com.bsuuv.grocerymanager.ui.util

import androidx.recyclerview.widget.DiffUtil
import com.bsuuv.grocerymanager.data.model.FoodItem

/**
 * Utility class for mutating one list of <code>FoodItem</code>s to another efficiently. This class
 * is used in this app's <code>RecyclerView Adapter</code>s in order to do as little re-rendering of
 * items as possible when updating the
 * <code>RecyclerView</code> contents.
 */
class FoodItemListDifferenceCalc {
    companion object {
        /**
         * Calculates an optimal set of update-operations to migrate from the old list to the new one.
         * @param oldFoodItems List to migrate from
         * @param newFoodItems List to migrate to
         * @return <code>DiffUtil.DiffResult</code>-object containing the update-operations.
         */
        fun calculateMigrationOperations(
            oldFoodItems: MutableList<out FoodItem>,
            newFoodItems: MutableList<out FoodItem>
        ): DiffUtil.DiffResult {
            return DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return oldFoodItems.size
                }

                override fun getNewListSize(): Int {
                    return newFoodItems.size
                }

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return oldFoodItems[oldItemPosition].id == newFoodItems[newItemPosition].id
                }

                override fun areContentsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
                    val oldFoodItem = oldFoodItems[oldItemPosition]
                    val newFoodItem = newFoodItems[newItemPosition]
                    return idAndVisibleMembersEqual(oldFoodItem, newFoodItem)
                }

            })
        }

        private fun idAndVisibleMembersEqual(
            oldFoodItem: FoodItem,
            newFoodItem: FoodItem
        ): Boolean {
            return oldFoodItem.id == newFoodItem.id &&
                    oldFoodItem.amount == newFoodItem.amount &&
                    oldFoodItem.brand == newFoodItem.brand &&
                    oldFoodItem.frequency == newFoodItem.frequency &&
                    oldFoodItem.imageUri == newFoodItem.imageUri &&
                    oldFoodItem.info == newFoodItem.info &&
                    oldFoodItem.label == newFoodItem.label &&
                    oldFoodItem.timeFrame == newFoodItem.timeFrame &&
                    oldFoodItem.unit == newFoodItem.unit
        }
    }
}