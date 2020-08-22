package com.bsuuv.grocerymanager.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bsuuv.grocerymanager.data.db.entity.FoodItemEntity

@Dao
interface FoodItemDao {

    @Insert
    fun insert(foodItem: FoodItemEntity)

    @Query("SELECT * FROM FoodItemEntity WHERE id = :foodItemId")
    fun get(foodItemId: Int): FoodItemEntity

    @Delete
    fun delete(foodItem: FoodItemEntity)

    @Query("SELECT * FROM FoodItemEntity ORDER BY label ASC")
    fun getAll(): LiveData<List<FoodItemEntity>>

    @Update
    fun update(foodItem: FoodItemEntity)
}