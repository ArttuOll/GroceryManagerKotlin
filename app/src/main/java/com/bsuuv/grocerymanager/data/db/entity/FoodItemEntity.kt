package com.bsuuv.grocerymanager.data.db.entity

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
        override val id: Int,

        @ColumnInfo(name = "image_uri")
        override val imageUri: String,

        @ColumnInfo(name = "label")
        override val label: String,

        @ColumnInfo(name = "brand")
        override val brand: String,

        @ColumnInfo(name = "info")
        override val info: String,

        @ColumnInfo(name = "amount")
        override val amount: Int,

        @ColumnInfo(name = "unit")
        override val unit: String,

        @TypeConverters(TimeFrameConverter::class)
        @ColumnInfo(name = "time_frame")
        override val timeFrame: TimeFrame,

        @ColumnInfo(name = "frequency")
        override val frequency: Int,

        @ColumnInfo(name = "countdown_value")
        override var countdownValue: Double
) : FoodItem
