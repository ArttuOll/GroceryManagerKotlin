package com.bsuuv.grocerymanager.data.model

import com.bsuuv.grocerymanager.util.TimeFrame

interface FoodItem {

    val id: Int
    val imageUri: String
    val label: String
    val brand: String
    val mInfo: String
    val amount: Int
    val unit: String
    val timeFrame: TimeFrame
    val frequency: Int
    val countdownValue: Double
}