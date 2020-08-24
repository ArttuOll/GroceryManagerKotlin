package com.bsuuv.grocerymanager.data

import android.app.Application
import androidx.lifecycle.LiveData
import com.bsuuv.grocerymanager.data.db.FoodItemDatabase
import com.bsuuv.grocerymanager.data.db.dao.FoodItemDao
import com.bsuuv.grocerymanager.data.db.entity.FoodItemEntity

class FoodItemRepository(application: Application) {

    private val mDao: FoodItemDao

    init {
        val db = FoodItemDatabase.getInstance(application)
        mDao = db.foodItemDao
    }

    suspend fun getFoodItems(): LiveData<MutableList<FoodItemEntity>> = mDao.getAll()

    suspend fun getFoodItem(id: Int): FoodItemEntity = mDao.get(id)

    suspend fun insert(foodItem: FoodItemEntity) = mDao.insert(foodItem)

    suspend fun delete(foodItem: FoodItemEntity) = mDao.delete(foodItem)

    suspend fun update(foodItem: FoodItemEntity) = mDao.update(foodItem)
}