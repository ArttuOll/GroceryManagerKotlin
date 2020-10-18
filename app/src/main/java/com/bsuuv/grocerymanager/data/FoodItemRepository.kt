package com.bsuuv.grocerymanager.data

import android.app.Application
import androidx.lifecycle.LiveData
import com.bsuuv.grocerymanager.data.db.FoodItemDatabase
import com.bsuuv.grocerymanager.data.db.dao.FoodItemDao
import com.bsuuv.grocerymanager.data.db.entity.FoodItemEntity

/**
 * A single source of data for the whole app. Handles communication with
 * [FoodItemDatabase] making sure that all operations are executed on a separate thread.
 */
class FoodItemRepository(application: Application) {

    private val mDao: FoodItemDao

    init {
        val db = FoodItemDatabase.getInstance(application)
        mDao = db.foodItemDao
    }

    /**
     * Returns an always-up-to-date list of all food-items created by the user, wrapped into an
     * observable `LiveData`-object.
     */
    fun getFoodItems(): LiveData<MutableList<FoodItemEntity>> = mDao.getAll()

    suspend fun getFoodItem(id: Int): FoodItemEntity = mDao.get(id)

    // TODO: jos halutaan optimoida, poistot voisi tehdä id:n perusteella, jolloin tätä varten
    //  esim. GroceryListStaten ei tarvitsisi säilyttää viitteitä kokonaisiin olioihin
    suspend fun insert(foodItem: FoodItemEntity) = mDao.insert(foodItem)

    suspend fun delete(foodItem: FoodItemEntity) = mDao.delete(foodItem)

    suspend fun deleteOneTimeItems() = mDao.deleteOneTimeItems()

    suspend fun update(foodItem: FoodItemEntity) = mDao.update(foodItem)
}