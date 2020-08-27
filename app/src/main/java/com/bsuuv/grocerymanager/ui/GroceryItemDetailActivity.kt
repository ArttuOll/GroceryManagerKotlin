package com.bsuuv.grocerymanager.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bsuuv.grocerymanager.R

/**
 * Activity for viewing the details of a grocery-item. Delegates the details of displaying a
 * grocery-item to {@link GroceryItemDetailFragment}. This is done to avoid code duplication, since
 * {@link GroceryItemDetailFragment} is also used to display grocery-item details in two-pane views
 * on large-screen devices (see {@link MainActivity}.
 *
 * @see GroceryItemDetailFragment
 * @see MainActivity
 */
class GroceryItemDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_item_detail)
        if (savedInstanceState == null) {
            val foodItemId = intent.getIntExtra(GroceryItemDetailFragment.FOOD_ITEM_ID_KEY, 0)
            launchFoodItemDetailFragment(foodItemId)
        }
    }

    private fun launchFoodItemDetailFragment(foodItemId: Int) {
        val fragment = GroceryItemDetailFragment.newInstance(foodItemId)
        supportFragmentManager
            .beginTransaction()
            .add(R.id.container_food_item_detail, fragment)
            .commit()
    }

}