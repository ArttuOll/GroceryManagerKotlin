package com.bsuuv.grocerymanager.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bsuuv.grocerymanager.R
import com.bsuuv.grocerymanager.ui.util.FoodItemCreationRequirementChecker
import com.bsuuv.grocerymanager.ui.util.RequestValidator
import com.bsuuv.grocerymanager.util.FrequencyQuotientCalc
import com.bsuuv.grocerymanager.util.SharedPreferencesHelper
import com.bsuuv.grocerymanager.util.TimeFrame
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NewFoodItemFragment : Fragment(), View.OnClickListener {

    private object Keys {
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
    private lateinit var navController: NavController
    @Inject lateinit var mSharedPrefsHelper: SharedPreferencesHelper
    private var mImageUri = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_new_food_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initMembers(view)
        setUpToggleButtons(view)
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initMembers(view: View) {
        navController = Navigation.findNavController(view)
        mLabelField = view.findViewById(R.id.editText_label)
        mBrandField = view.findViewById(R.id.editText_brand)
        mAmountField = view.findViewById(R.id.editText_amount)
        mInfoField = view.findViewById(R.id.editText_info)
        mFrequencyField = initFrequencyEditText(view)
        mImage = view.findViewById(R.id.imageView_new_fooditem)
        mImage.setOnClickListener(this)
        mUnitDropdown = initUnitDropdown(view)
        mTimeFrameButtons = view.findViewById(R.id.freq_selection_togglegroup)
        val fab = view.findViewById<FloatingActionButton>(R.id.fab_new_fooditem)
        fab.setOnClickListener(this)
    }

    private fun initFrequencyEditText(view: View): EditText {
        val editText: EditText = view.findViewById(R.id.editText_freq)
        editText.setText(getString(R.string.freq_edittext_default))
        return editText
    }

    private fun initUnitDropdown(view: View): AutoCompleteTextView {
        val unitDropdown: AutoCompleteTextView = view.findViewById(R.id.new_fooditem_unit_dropdown)
        val dropdownAdapter =
            ArrayAdapter(
                requireContext(),
                R.layout.new_food_item_unit_dropdown_item,
                resources.getStringArray(R.array.units_plural)
            )
        unitDropdown.setAdapter(dropdownAdapter)
        return unitDropdown
    }

    private fun setUpToggleButtons(view: View) {
        setUpWeekToggle(view)
        setUpTwoWeeksToggle(view)
        setUpMonthToggle(view)
    }

    private fun setUpWeekToggle(view: View) {
        val weekToggle: Button = view.findViewById(R.id.togglebutton_week)
        weekToggle.setText(R.string.button_week)
        weekToggle.setOnClickListener(this)
    }

    private fun setUpTwoWeeksToggle(view: View) {
        val twoWeekToggle: Button = view.findViewById(R.id.togglebutton_two_weeks)
        twoWeekToggle.setText(R.string.button_twoweeks)
        twoWeekToggle.setOnClickListener(this)
    }

    private fun setUpMonthToggle(view: View) {
        val monthToggle: Button = view.findViewById(R.id.togglebutton_month)
        monthToggle.setText(R.string.button_month)
        monthToggle.setOnClickListener(this)
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
        ) run {
//            NewFoodItemActivity.launchConfigurationsActivity(
//                textFieldValues,
//                amount,
//                timeFrame,
//                frequency,
//                frequencyQuotient
//            )
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
        Snackbar.make(
            requireView().findViewById(R.id.fab_new_fooditem),
            messageResId,
            Snackbar.LENGTH_LONG
        )
            .setAnchorView(R.id.fab_new_fooditem).show()
    }

//    fun onImageClick(view: View) {
//        val cameraUtil = CameraUtil(requireContext())
//        mImageUri = cameraUtil.getImagePath()
//        val toCaptureImage = cameraUtil.getIntentToCaptureImage()
//        if (cameraUtil.cameraAppExists(toCaptureImage)) launchCameraApp(toCaptureImage)
//    }

    private fun launchCameraApp(toCaptureImage: Intent) {
        startActivityForResult(toCaptureImage, RequestValidator.REQUEST_IMAGE_CAPTURE)
    }

    /**
     * Called when one of the toggle buttons for time frame selection is clicked.
     */
    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.togglebutton_week -> mTimeFrameButtons.check(R.id.togglebutton_week)
            R.id.togglebutton_two_weeks -> mTimeFrameButtons.check(R.id.togglebutton_two_weeks)
            R.id.togglebutton_month -> mTimeFrameButtons.check(R.id.togglebutton_month)
            R.id.fab_new_fooditem -> activity?.onBackPressed()
            R.id.imageView_new_fooditem -> {
                // TODO: launch camera activity
            }
        }
    }
}