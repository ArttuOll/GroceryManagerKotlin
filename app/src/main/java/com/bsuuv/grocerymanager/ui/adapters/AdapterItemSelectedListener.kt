package com.bsuuv.grocerymanager.ui.adapters

import android.widget.ImageView
import com.bsuuv.grocerymanager.data.model.FoodItem

interface AdapterItemSelectedListener {
    fun onItemSelected(item: FoodItem, imageView: ImageView)
}