package com.bsuuv.grocerymanager.data.db.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.bsuuv.grocerymanager.data.model.FoodItem
import com.bsuuv.grocerymanager.util.TimeFrame

@Entity
data class FoodItemEntity(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        override val mId: Int,

        @ColumnInfo(name = "image_uri")
        override val mImageUri: String,

        @NonNull
        @ColumnInfo(name = "label")
        override val label: String,

        @ColumnInfo(name = "brand")
        override val mBrand: String,

        @ColumnInfo(name = "info")
        override val mInfo: String,

        @ColumnInfo(name = "amount")
        override val mAmount: Int,

        @ColumnInfo(name = "unit")
        override val mUnit: String,

        @NonNull
        @TypeConverters(TimeFrameConverter::class)
        @ColumnInfo(name = "time_frame")
        override val mTimeFrame: TimeFrame,

        @ColumnInfo(name = "frequency")
        override val mFrequency: Int,

        @ColumnInfo(name = "countdown_value")
        override val mCountdownValue: Double
) : FoodItem
