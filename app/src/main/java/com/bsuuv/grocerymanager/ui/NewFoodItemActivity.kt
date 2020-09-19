package com.bsuuv.grocerymanager.ui

import android.content.Intent
import android.os.Bundle
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bsuuv.grocerymanager.R
import com.bsuuv.grocerymanager.ui.util.FoodItemCreationRequirementChecker
import com.bsuuv.grocerymanager.ui.util.ImageViewPopulater
import com.bsuuv.grocerymanager.ui.util.RequestValidator
import com.bsuuv.grocerymanager.util.SharedPreferencesHelper
import com.bsuuv.grocerymanager.util.TimeFrame
import com.google.android.material.button.MaterialButtonToggleGroup
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

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
@AndroidEntryPoint
class NewFoodItemActivity : AppCompatActivity() {

    private object Keys {
        const val IMAGE_PATH_KEY = "imagePath"
        const val FREQUENCY_NOT_SET = 0
    }

    private lateinit var mTimeFrameButtons: MaterialButtonToggleGroup
    private lateinit var mLabelField: EditText
    private lateinit var mBrandField: EditText
    private lateinit var mAmountField: EditText
    private lateinit var mInfoField: EditText
    private lateinit var mFrequencyField: EditText
    private lateinit var mImage: ImageView
    private lateinit var mUnitDropdown: AutoCompleteTextView
    @Inject lateinit var mSharedPrefsHelper: SharedPreferencesHelper
    private var mImageUri = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_food_item_host)

        val fragment = NewFoodItemFragment()
        fragment.arguments = intent.extras
        supportFragmentManager
            .beginTransaction()
            .add(R.id.new_fooditem_content, fragment)
            .commit()

        title = getString(R.string.newFoodItem_title)
        manageIntent()
        if (savedInstanceState != null) recoverFoodImage(savedInstanceState)
    }

    private fun manageIntent() {
        val fromConfigs = intent
        if (intentsToEditFoodItem(fromConfigs)) {
            setInputFieldValuesFromIntent(fromConfigs)
            setImageFromIntentAndSaveUri(fromConfigs)
            setToggleButtonStatesFromIntent(fromConfigs)
        }
    }

    private fun intentsToEditFoodItem(intent: Intent?) = intent?.getIntExtra(
        "requestCode",
        RequestValidator.NONE
    ) == RequestValidator.FOOD_ITEM_EDIT_REQUEST

    private fun setInputFieldValuesFromIntent(intent: Intent?) {
        mLabelField.setText(intent?.getStringExtra("label"))
        mBrandField.setText(intent?.getStringExtra("brand"))
        mAmountField.setText(intent?.getIntExtra("amount", 0).toString())
        mUnitDropdown.setText(intent?.getStringExtra("unit"))
        mInfoField.setText(intent?.getStringExtra("info"))
        mFrequencyField.setText(
            intent?.getIntExtra(
                "frequency",
                Keys.FREQUENCY_NOT_SET
            ).toString()
        )
    }

    private fun setImageFromIntentAndSaveUri(intent: Intent?) {
        val uri = intent?.getStringExtra("uri")!!
        mImageUri = uri
        ImageViewPopulater.populateFromUri(this, mImageUri, mImage)
    }

    private fun setToggleButtonStatesFromIntent(intent: Intent?) {
        when (intent?.getSerializableExtra("time_frame")) {
            TimeFrame.WEEK -> mTimeFrameButtons.check(R.id.togglebutton_week)
            TimeFrame.TWO_WEEKS -> mTimeFrameButtons.check(R.id.togglebutton_two_weeks)
            TimeFrame.MONTH -> mTimeFrameButtons.check(R.id.togglebutton_month)
        }
    }

    private fun recoverFoodImage(savedInstanceState: Bundle) {
        mImageUri = savedInstanceState.getString(Keys.IMAGE_PATH_KEY)!!
        if (mImageUri != "") ImageViewPopulater.populateFromUri(this, mImageUri, mImage)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (RequestValidator.imageCaptureSuccesful(requestCode, resultCode)) {
            ImageViewPopulater.populateFromUri(this, mImageUri, mImage)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(Keys.IMAGE_PATH_KEY, mImageUri)
        super.onSaveInstanceState(outState)
    }

    fun launchConfigurationsActivity(
        textFieldValues: MutableList<String>,
        amount: Int,
        timeFrame: TimeFrame,
        frequency: Int,
        frequencyQuotient: Double
    ) {
        val toConfigs = createIntentToConfigs(
            textFieldValues,
            amount,
            timeFrame,
            frequency,
            frequencyQuotient
        )
        setResult(RESULT_OK, toConfigs)
        finish()
    }

    private fun createIntentToConfigs(
        textFieldValues: MutableList<String>,
        amount: Int,
        timeFrame: TimeFrame,
        frequency: Int,
        frequencyQuotient: Double
    ): Intent {
        val toConfigs = Intent(this, ConfigurationsActivity::class.java)
        toConfigs.putExtra("label", textFieldValues[0])
        toConfigs.putExtra("brand", textFieldValues[1])
        toConfigs.putExtra("amount", amount)
        toConfigs.putExtra("unit", textFieldValues[2])
        toConfigs.putExtra("info", textFieldValues[3])
        toConfigs.putExtra("time_frame", timeFrame)
        toConfigs.putExtra("frequency", frequency)
        toConfigs.putExtra("uri", mImageUri)
        toConfigs.putExtra("id", getEditedFoodItemId())
        toConfigs.putExtra("countdownValue", getEditedFoodItemCountdownValue())
        toConfigs.putExtra("frequencyQuotient", frequencyQuotient)
        return toConfigs
    }

    private fun getEditedFoodItemId(): Int {
        return intent.getIntExtra("id", 0)
    }

    private fun getEditedFoodItemCountdownValue(): Double {
        return intent.getDoubleExtra("count", 0.0)
    }

}