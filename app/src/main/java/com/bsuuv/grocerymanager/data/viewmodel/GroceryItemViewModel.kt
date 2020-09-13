package com.bsuuv.grocerymanager.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.liveData
import com.bsuuv.grocerymanager.data.FoodItemRepository
import com.bsuuv.grocerymanager.data.GroceryListExtractor
import com.bsuuv.grocerymanager.data.GroceryListState
import com.bsuuv.grocerymanager.data.db.entity.FoodItemEntity
import com.bsuuv.grocerymanager.ui.MainActivity
import com.bsuuv.grocerymanager.util.DateTimeHelper
import com.bsuuv.grocerymanager.util.SharedPreferencesHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * A `ViewModel` that contains all the data and business logic calls required by [MainActivity].
 */
class GroceryItemViewModel(application: Application) : AndroidViewModel
    (application) {

    private val mRepository: FoodItemRepository
    private val mGroceryListExtractor: GroceryListExtractor
    private val mDateTimeHelper: DateTimeHelper
    private val mGroceryListState: GroceryListState

    init {
        val sharedPrefsHelper = SharedPreferencesHelper(application)
        mRepository = FoodItemRepository(application)
        mGroceryListState = GroceryListState(sharedPrefsHelper)
        mGroceryListExtractor = GroceryListExtractor(mGroceryListState, sharedPrefsHelper)
        mDateTimeHelper = DateTimeHelper(application, sharedPrefsHelper)
        if (!mDateTimeHelper.isGroceryDay()) updateDatabase()
    }

    private fun updateDatabase() {
        for (foodItem in mGroceryListState.incrementedItems) {
            CoroutineScope(IO).launch {
                mRepository.update(foodItem)
            }
        }
        mGroceryListState.reset()
    }

    /**
     * Returns an always-up-to-date list of all food-items that are qualified to be on the grocery
     * list.
     */
    fun getGroceryList(): LiveData<MutableList<FoodItemEntity>> {
        val foodItems = liveData { emitSource(mRepository.getFoodItems()) }
        return Transformations.map(
            foodItems,
            mGroceryListExtractor::extractGroceryListFromFoodItems
        )
    }

    fun get(id: Int): FoodItemEntity = runBlocking {
        mRepository.getFoodItem(id)
    }

    /**
     * Deletes the given food-item from the grocery list, but not from the database.
     */
    fun deleteFromGroceryList(foodItem: FoodItemEntity) = mGroceryListState.remove(foodItem)

    override fun onCleared() {
        super.onCleared()
        if (mDateTimeHelper.isGroceryDay()) mGroceryListState.save()
    }
}