package com.bsuuv.grocerymanager.data.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
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
class GroceryItemViewModel @ViewModelInject constructor(application: Application) :
    AndroidViewModel(application) {

    private val repository: FoodItemRepository
    private val groceryListExtractor: GroceryListExtractor
    private val dateTimeHelper: DateTimeHelper
    private val groceryListState: GroceryListState

    init {
        val sharedPrefsHelper = SharedPreferencesHelper(application)
        repository = FoodItemRepository(application)
        groceryListState = GroceryListState(sharedPrefsHelper)
        groceryListExtractor = GroceryListExtractor(groceryListState, sharedPrefsHelper)
        dateTimeHelper = DateTimeHelper(application, sharedPrefsHelper)
    }

    fun onGroceryDayPassed() {
        updateItemCountdownValues()
        deleteOneTimeItems()
        groceryListState.reset()
    }

    private fun updateItemCountdownValues() {
        for (foodItem in groceryListState.incrementedItems) {
            CoroutineScope(IO).launch {
                repository.update(foodItem)
            }
        }
    }

    private fun deleteOneTimeItems() {
        CoroutineScope(IO).launch {
            repository.deleteOneTimeItems()
        }
    }

    /**
     * Returns an always-up-to-date list of all food-items that are qualified to be on the grocery
     * list.
     */
    fun getGroceryList(): LiveData<MutableList<FoodItemEntity>> {
        val foodItems = liveData { emitSource(repository.getFoodItems()) }
        return Transformations.map(
            foodItems,
            groceryListExtractor::extractGroceryListFromFoodItems
        )
    }

    fun get(id: Int): FoodItemEntity = runBlocking {
        repository.getFoodItem(id)
    }

    /**
     * Deletes the given food-item from the grocery list, but not from the database.
     */
    fun deleteFromGroceryList(foodItem: FoodItemEntity) = groceryListState.remove(foodItem)

    override fun onCleared() {
        super.onCleared()
        if (dateTimeHelper.isGroceryDay()) groceryListState.save()
    }
}