package com.bsuuv.grocerymanager.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bsuuv.grocerymanager.data.db.entity.FoodItemEntity

@Dao
interface FoodItemDao {

    @Insert
    suspend fun insert(foodItem: FoodItemEntity)

    @Query("SELECT * FROM FoodItemEntity WHERE id = :foodItemId")
    suspend fun get(foodItemId: Int): FoodItemEntity

    @Delete
    suspend fun delete(foodItem: FoodItemEntity)

    @Query("SELECT * FROM FoodItemEntity ORDER BY label ASC")
    fun getAll(): LiveData<MutableList<FoodItemEntity>>

    @Update
    suspend fun update(foodItem: FoodItemEntity)
}