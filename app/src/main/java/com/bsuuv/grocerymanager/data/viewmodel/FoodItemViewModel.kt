package com.bsuuv.grocerymanager.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.bsuuv.grocerymanager.data.FoodItemRepository
import com.bsuuv.grocerymanager.data.db.entity.FoodItemEntity
import com.bsuuv.grocerymanager.ui.ConfigurationsActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * A `ViewModel` that contains all the data and business logic calls required by [ConfigurationsActivity].
 */
class FoodItemViewModel(application: Application) : AndroidViewModel(application) {

    private val mRepository: FoodItemRepository = FoodItemRepository(application)

    fun getFoodItems(): LiveData<MutableList<FoodItemEntity>> =
        liveData { emitSource(mRepository.getFoodItems()) }

    fun get(id: Int): FoodItemEntity = runBlocking {
        mRepository.getFoodItem(id)
    }

    fun insert(foodItem: FoodItemEntity) = CoroutineScope(IO).launch {
        mRepository.insert(foodItem)
    }

    fun delete(foodItem: FoodItemEntity) = CoroutineScope(IO).launch {
        mRepository.delete(foodItem)
    }

    fun update(foodItem: FoodItemEntity) = CoroutineScope(IO).launch {
        mRepository.update(foodItem)
    }
}