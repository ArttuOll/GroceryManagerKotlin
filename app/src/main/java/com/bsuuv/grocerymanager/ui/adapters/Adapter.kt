package com.bsuuv.grocerymanager.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import com.bsuuv.grocerymanager.data.model.FoodItem
import com.bsuuv.grocerymanager.ui.util.FoodItemListDifferenceCalc

/**
 * A generic adapter class containing common functionality of all adapters of the app.
 */
abstract class Adapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var mItems: MutableList<out FoodItem>

    override fun getItemCount() = if (this::mItems.isInitialized) mItems.size else 0

    fun getItemAtPosition(position: Int) = mItems[position]

    fun setItems(newFoodItems: MutableList<out FoodItem>) {
        if (!this::mItems.isInitialized) initItems(newFoodItems)
        else updateItems(newFoodItems)
    }

    private fun initItems(newItems: MutableList<out FoodItem>) {
        mItems = newItems
        notifyItemRangeInserted(0, newItems.size)
    }

    private fun updateItems(newItems: MutableList<out FoodItem>) {
        val migrationOperations =
            FoodItemListDifferenceCalc.calculateMigrationOperations(mItems, newItems)
        mItems = newItems
        migrationOperations.dispatchUpdatesTo(this)
    }
}