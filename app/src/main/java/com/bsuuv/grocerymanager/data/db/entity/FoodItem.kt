package com.bsuuv.grocerymanager.data.db.entity

import com.bsuuv.grocerymanager.util.TimeFrame

interface FoodItem {

    val mId: Int
    val mImageUri: String
    val label: String
    val mBrand: String
    val mInfo: String
    val mAmount: Int
    val mUnit: String
    val mTimeFrame: TimeFrame
    val mFrequency: Int
    val mCountdownValue: Double
}