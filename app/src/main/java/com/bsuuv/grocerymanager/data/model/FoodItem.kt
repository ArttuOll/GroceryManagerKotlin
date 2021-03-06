package com.bsuuv.grocerymanager.data.model

import com.bsuuv.grocerymanager.util.TimeFrame

/**
 * Definition of a food-item on the grocery list.
 */
interface FoodItem {

    val id: Int
    val imageUri: String
    val label: String
    val brand: String
    val info: String
    val amount: Int
    val unit: String
    val timeFrame: TimeFrame
    val frequency: Int
    val countdownValue: Double
    val onetimeItem: Boolean
}