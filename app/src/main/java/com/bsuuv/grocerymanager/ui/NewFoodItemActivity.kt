package com.bsuuv.grocerymanager.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bsuuv.grocerymanager.R
import com.bsuuv.grocerymanager.ui.util.CameraUtil
import com.bsuuv.grocerymanager.ui.util.FoodItemCreationRequirementChecker
import com.bsuuv.grocerymanager.ui.util.ImageViewPopulater
import com.bsuuv.grocerymanager.ui.util.RequestValidator
import com.bsuuv.grocerymanager.util.FrequencyQuotientCalc
import com.bsuuv.grocerymanager.util.SharedPreferencesHelper
import com.bsuuv.grocerymanager.util.TimeFrame
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.snackbar.Snackbar

class NewFoodItemActivity : AppCompatActivity(), View.OnClickListener {

    private object Keys {
        const val IMAGE_PATH_KEY = "imagePath"
        const val FREQUENCY_NOT_SET = 0
        const val AMOUNT_FIELD_EMPTY = 0
    }

    private lateinit var mTimeFrameButtons: MaterialButtonToggleGroup
    private lateinit var mLabelField: EditText
    private lateinit var mBrandField: EditText
    private lateinit var mAmountField: EditText
    private lateinit var mInfoField: EditText
    private lateinit var mFrequencyField: EditText
    private lateinit var mImage: ImageView
    private lateinit var mUnitDropdown: AutoCompleteTextView
    private lateinit var mSharedPrefsHelper: SharedPreferencesHelper
    private lateinit var mImageUri: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_food_item)
        title = getString(R.string.newFoodItem_title)
        initMembers()
        setUpToggleButtons()
        manageIntent()
        if (savedInstanceState != null) recoverFoodImage(savedInstanceState)
    }

    private fun initMembers() {
        mLabelField = findViewById(R.id.editText_label)
        mBrandField = findViewById(R.id.editText_brand)
        mAmountField = findViewById(R.id.editText_amount)
        mInfoField = findViewById(R.id.editText_info)
        mFrequencyField = initFrequencyEditText()
        mImage = findViewById(R.id.imageView_new_fooditem)
        mSharedPrefsHelper = SharedPreferencesHelper(this)
        mUnitDropdown = initUnitDropdown()
        mTimeFrameButtons = findViewById(R.id.freq_selection_togglegroup)
    }

    private fun initFrequencyEditText(): EditText {
        val editText: EditText = findViewById(R.id.editText_freq)
        editText.setText(getString(R.string.freq_edittext_default))
        return editText
    }

    private fun initUnitDropdown(): AutoCompleteTextView {
        val unitDropdown: AutoCompleteTextView = findViewById(R.id.new_fooditem_unit_dropdown)
        val dropdownAdapter =
            ArrayAdapter(
                this,
                R.layout.new_food_item_unit_dropdown_item,
                resources.getStringArray(R.array.units_plural)
            )
        unitDropdown.setAdapter(dropdownAdapter)
        return unitDropdown
    }

    private fun setUpToggleButtons() {
        setUpWeekToggle()
        setUpTwoWeeksToggle()
        setUpMonthToggle()
    }

    private fun setUpWeekToggle() {
        val weekToggle: Button = findViewById(R.id.togglebutton_week)
        weekToggle.setText(R.string.button_week)
        weekToggle.setOnClickListener(this)
    }

    private fun setUpTwoWeeksToggle() {
        val twoWeekToggle: Button = findViewById(R.id.togglebutton_two_weeks)
        twoWeekToggle.setText(R.string.button_twoweeks)
        twoWeekToggle.setOnClickListener(this)
    }

    private fun setUpMonthToggle() {
        val monthToggle: Button = findViewById(R.id.togglebutton_month)
        monthToggle.setText(R.string.button_month)
        monthToggle.setOnClickListener(this)
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
        ImageViewPopulater.populateFromUri(this, mImageUri, mImage)
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

    fun onFabClick(view: View) {
        val label = mLabelField.text.toString()
        val brand = mBrandField.text.toString()
        val unit = mUnitDropdown.text.toString()
        val info = mInfoField.text.toString()
        val textFieldValues = mutableListOf(label, brand, unit, info)
        val timeFrame = getActiveToggleButton()
        val amount = getAmount()
        val frequency = getFrequency()
        val groceryDaysAWeek = mSharedPrefsHelper.getGroceryDays().size
        val frequencyQuotient =
            FrequencyQuotientCalc.calculate(frequency, timeFrame, groceryDaysAWeek)

        if (foodItemCreationRequirementsMet(
                textFieldValues,
                amount,
                timeFrame,
                frequency,
                frequencyQuotient
            )
        ) {
            launchConfigurationsActivity(
                textFieldValues,
                amount,
                timeFrame,
                frequency,
                frequencyQuotient
            )
        }
    }

    private fun getActiveToggleButton(): TimeFrame {
        return when (mTimeFrameButtons.checkedButtonId) {
            R.id.togglebutton_week -> TimeFrame.WEEK
            R.id.togglebutton_two_weeks -> TimeFrame.TWO_WEEKS
            R.id.togglebutton_month -> TimeFrame.MONTH
            else -> TimeFrame.NULL
        }
    }

    private fun getAmount(): Int {
        val amountString = mAmountField.text.toString()
        return if (amountString == "") Keys.AMOUNT_FIELD_EMPTY else amountString.toInt()
    }

    private fun getFrequency(): Int {
        val frequencyString = mFrequencyField.text.toString()
        return if (frequencyString == "") Keys.FREQUENCY_NOT_SET else frequencyString.toInt()
    }

    private fun foodItemCreationRequirementsMet(
        textFieldValues: MutableList<String>,
        amount: Int,
        timeFrame: TimeFrame,
        frequency: Int,
        frequencyQuotient: Double
    ): Boolean {
        val checker = FoodItemCreationRequirementChecker(mSharedPrefsHelper)
        return try {
            checker.requirementsMet(
                textFieldValues,
                amount,
                timeFrame,
                frequency,
                frequencyQuotient
            )
        } catch (e: FoodItemCreationRequirementChecker.RequirementNotMetException) {
            showSnackbar(e.messageResId)
            false
        }
    }

    private fun showSnackbar(messageResId: Int) {
        Snackbar.make(findViewById(R.id.fab_new_fooditem), messageResId, Snackbar.LENGTH_LONG)
            .setAnchorView(R.id.fab_new_fooditem).show()
    }

    private fun launchConfigurationsActivity(
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
        toConfigs.putExtra("unit", textFieldValues[3])
        toConfigs.putExtra("info", textFieldValues[4])
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

    fun onImageClick(view: View) {
        val cameraUtil = CameraUtil(this)
        mImageUri = cameraUtil.getImagePath()
        val toCaptureImage = cameraUtil.getIntentToCaptureImage()
        if (cameraUtil.cameraAppExists(toCaptureImage)) launchCameraApp(toCaptureImage)
    }

    private fun launchCameraApp(toCaptureImage: Intent) {
        startActivityForResult(toCaptureImage, RequestValidator.REQUEST_IMAGE_CAPTURE)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.togglebutton_week -> mTimeFrameButtons.check(R.id.togglebutton_week)
            R.id.togglebutton_two_weeks -> mTimeFrameButtons.check(R.id.togglebutton_two_weeks)
            R.id.togglebutton_month -> mTimeFrameButtons.check(R.id.togglebutton_month)
        }
    }
}