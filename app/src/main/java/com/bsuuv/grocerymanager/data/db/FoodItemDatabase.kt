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
 * A <code>Room</code> database definition for saving food-items.
 *
 * @see <a href=https://developer.android.com/topic/libraries/architecture/room>Room Persistence
 * Library</a>
 */
@Database(entities = [FoodItemEntity::class], version = 1)
@TypeConverters(TimeFrameConverter::class)
abstract class FoodItemDatabase : RoomDatabase() {

    /**
     * Defines the type of DAO this <code>Room</code> database should provide.
     *
     * @return <code>FoodItemDao</code> that grants access to values stored in
     * this database
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
                    ).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}