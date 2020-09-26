package com.bsuuv.grocerymanager.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bsuuv.grocerymanager.R
import com.bsuuv.grocerymanager.ui.util.FoodItemCreationRequirementChecker

/**
 * Activity for creating new food-items. Displays a form with input fields corresponding to
 * different properties of a food-item:
 *
 * * An image, with a camera icon as a placeholder. Clicking the camera icon
 *     or an image launches the devices camera app, if one is installed.
 * * A label
 * * Brand
 * * Amount
 * * Unit for the amount
 * * Frequency, that is, how many times in the chosen time frame (see next)
 *     the user wants this?
 * * Time frame, that is, what is the time period during which the user wants
 *     to see this food-item appear on the grocery list the amount of times specified
 *     by the frequency?
 * * Additional information the user wants to add
 *
 * Some of these input fields are not mandatory. Finally, the view contains a
 * floating action button, which launches validation of the input fields and
 * afterwards sends their data to [ConfigurationsActivity]. For information
 * on required fields and their validation, see [FoodItemCreationRequirementChecker]
 */
class NewFoodItemActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_food_item_host)
    }
}
