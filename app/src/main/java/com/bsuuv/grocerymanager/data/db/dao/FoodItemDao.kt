package com.bsuuv.grocerymanager.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bsuuv.grocerymanager.data.db.FoodItemDatabase
import com.bsuuv.grocerymanager.data.db.entity.FoodItemEntity

/**
 * Data-access-object definition for accessing food-items in the [FoodItemDatabase]. The
 * actual implementation is generated automatically by the `Room Persistence Library`.
 */
@Dao
interface FoodItemDao {

    @Insert
    suspend fun insert(foodItem: FoodItemEntity)

    @Query("SELECT * FROM FoodItemEntity WHERE id = :foodItemId")
    suspend fun get(foodItemId: Int): FoodItemEntity

    @Delete
    suspend fun delete(foodItem: FoodItemEntity)

    @Query("DELETE FROM FoodItemEntity WHERE onetime_item = 1")
    suspend fun deleteOneTimeItems()

    @Query("SELECT * FROM FoodItemEntity ORDER BY label ASC")
    fun getAll(): LiveData<MutableList<FoodItemEntity>>

    @Update
    suspend fun update(foodItem: FoodItemEntity)
}