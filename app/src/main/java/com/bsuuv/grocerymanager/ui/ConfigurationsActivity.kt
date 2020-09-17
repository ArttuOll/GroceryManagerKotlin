package com.bsuuv.grocerymanager.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bsuuv.grocerymanager.R
import com.bsuuv.grocerymanager.data.db.entity.FoodItemEntity
import com.bsuuv.grocerymanager.data.viewmodel.FoodItemViewModel
import com.bsuuv.grocerymanager.ui.adapters.ConfigurationsListAdapter
import com.bsuuv.grocerymanager.ui.util.RequestValidator
import com.bsuuv.grocerymanager.util.TimeFrame

/**
 * Activity for viewing all created food-items. Displays the items as a list or shows a placeholder
 * text if no food-items are yet created. The items can be swiped left or right to delete them, or
 * clicked to edit the. Contains a floating action button, which launches
 * [NewFoodItemActivity] for creating a new food-item.
 * <p>
 * The food-items are displayed in a `RecyclerView`, the
 * [ConfigurationsListAdapter] of which receives its data from a [FoodItemViewModel].
 */
class ConfigurationsActivity : AppCompatActivity() {

    private lateinit var mViewModel: FoodItemViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.configs_list_host)

        if (savedInstanceState == null) {
            val fragment = ConfigsListFragment()
            fragment.arguments = intent.extras
            supportFragmentManager
                .beginTransaction()
                .add(R.id.configs_content, fragment)
                .commit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, fromNewFoodItem: Intent?) {
        super.onActivityResult(requestCode, resultCode, fromNewFoodItem)
        if (RequestValidator.foodItemCreationSuccesful(requestCode, resultCode)) {
            val result = getFoodItemFromIntent(fromNewFoodItem)
            mViewModel.insert(result)
        } else if (RequestValidator.foodItemEditSuccesful(requestCode, resultCode)) {
            val id = intent?.getIntExtra("id", 0)!!
            val result = getFoodItemFromIntent(fromNewFoodItem, id = id)
            mViewModel.update(result)
        }
    }

    private fun getFoodItemFromIntent(fromNewFoodItem: Intent?, id: Int = 0): FoodItemEntity {
        val label = fromNewFoodItem?.getStringExtra("label")!!
        val brand = fromNewFoodItem.getStringExtra("brand")!!
        val amount = fromNewFoodItem.getIntExtra("amount", 0)
        val unit = fromNewFoodItem.getStringExtra("unit")!!
        val info = fromNewFoodItem.getStringExtra("info")!!
        val timeFrame = fromNewFoodItem.getSerializableExtra("time_frame")!!
        val frequency = fromNewFoodItem.getIntExtra("frequency", 0)
        val imageUri = fromNewFoodItem.getStringExtra("uri")!!
        val countdownValue = fromNewFoodItem.getDoubleExtra("frequencyQuotient", 0.0)
        return FoodItemEntity(
            id,
            imageUri,
            label,
            brand,
            info,
            amount,
            unit,
            timeFrame as TimeFrame,
            frequency,
            countdownValue
        )
    }

    /**
     * Called when the floating action button in this activity is pressed. Launches
     * `NewFoodItemActivity` for creating a new `FoodItem`.
     */
    fun onFabClick(view: View) {
        val toNewFoodItem = Intent(this, NewFoodItemActivity::class.java)
        val requestCode = RequestValidator.FOOD_ITEM_CREATE_REQUEST
        toNewFoodItem.putExtra("requestCode", requestCode)
        startActivityForResult(toNewFoodItem, requestCode)
    }
}