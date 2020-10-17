package com.bsuuv.grocerymanager.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bsuuv.grocerymanager.data.db.dao.FoodItemDao
import com.bsuuv.grocerymanager.data.db.entity.FoodItemEntity
import com.bsuuv.grocerymanager.data.db.entity.TimeFrameConverter

/**
 * A `Room` database definition for saving food-items.
 */
@Database(entities = [FoodItemEntity::class], version = 2)
@TypeConverters(TimeFrameConverter::class)
abstract class FoodItemDatabase : RoomDatabase() {

    /**
     * Returns a `FoodItemDao`-object that grants access to values stored in
     * this database.
     */
    abstract val foodItemDao: FoodItemDao

    companion object {
        @Volatile
        private var INSTANCE: FoodItemDatabase? = null

        fun getInstance(context: Context): FoodItemDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        FoodItemDatabase::class.java,
                        "fooditem_database"
                    ).fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}