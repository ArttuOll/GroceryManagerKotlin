package com.bsuuv.grocerymanager.ui.adapters

import android.widget.ImageView
import com.bsuuv.grocerymanager.data.model.FoodItem

/**
 * Defines what is done when an item in an adapter is clicked. This is not inside the fragments and
 * is delegated to the host fragments because the have access to the safe-args classes.
 */
interface AdapterItemSelectedListener {
    fun onItemSelected(item: FoodItem, imageView: ImageView)
}