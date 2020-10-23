package com.bsuuv.grocerymanager.ui

import android.content.Intent
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.bsuuv.grocerymanager.R
import com.bsuuv.grocerymanager.data.db.entity.FoodItemEntity
import com.bsuuv.grocerymanager.data.viewmodel.FoodItemViewModel
import com.bsuuv.grocerymanager.ui.util.*
import com.bsuuv.grocerymanager.util.FrequencyQuotientCalc
import com.bsuuv.grocerymanager.util.SharedPreferencesHelper
import com.bsuuv.grocerymanager.util.TimeFrame
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


/**
 * Fragment for creating new food-items. Displays a form with input fields corresponding to
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
 * afterwards sends their data to [ConfigsListFragment]. For information
 * on required fields and their validation, see [FoodItemCreationRequirementChecker]
 */
@AndroidEntryPoint
class NewFoodItemFragment : Fragment(), View.OnClickListener {

    private companion object {
        const val IMAGE_PATH_KEY = "imagePath"
        const val FREQUENCY_NOT_SET = 0
        const val AMOUNT_FIELD_EMPTY = 0
        const val ID_NOT_SET = 0
    }

    private lateinit var timeFrameButtons: MaterialButtonToggleGroup
    private lateinit var labelField: EditText
    private lateinit var brandField: EditText
    private lateinit var amountField: EditText
    private lateinit var infoField: EditText
    private lateinit var frequencyField: EditText
    private lateinit var imageView: ImageView
    private lateinit var unitDropdown: AutoCompleteTextView
    private lateinit var navController: NavController
    private lateinit var editedItem: FoodItemEntity
    private var imageUri = ""
    private val foodItemViewModel: FoodItemViewModel by viewModels()
    private val args: NewFoodItemFragmentArgs by navArgs()
    @Inject lateinit var sharedPrefsHelper: SharedPreferencesHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(
            android
                .R.transition.move
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (savedInstanceState != null) recoverFoodImage(savedInstanceState)
        return inflater.inflate(R.layout.fragment_new_fooditem, container, false)
    }

    private fun recoverFoodImage(savedInstanceState: Bundle) {
        imageUri = savedInstanceState.getString(IMAGE_PATH_KEY)!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initMembers(view)
        setUpToggleButtons(view)
        manageIntention()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun manageIntention() {
        if (args.intention == Intention.EDIT) {
            val editedItemId = args.editedItemId
            editedItem = foodItemViewModel.get(editedItemId)
            populateInputFields()
            populateImageView()
            setToggleButtonStates()
        }
    }

    private fun populateInputFields() {
        labelField.setText(editedItem.label)
        brandField.setText(editedItem.brand)
        amountField.setText(editedItem.amount.toString())
        unitDropdown.setText(editedItem.unit)
        infoField.setText(editedItem.info)
        frequencyField.setText(editedItem.frequency.toString())
    }

    private fun populateImageView() {
        val uri = editedItem.imageUri
        ImageViewPopulater.populateFromUri(requireContext(), uri, imageView)
    }

    private fun setToggleButtonStates() {
        when (editedItem.timeFrame) {
            TimeFrame.WEEK -> timeFrameButtons.check(R.id.togglebutton_week)
            TimeFrame.TWO_WEEKS -> timeFrameButtons.check(R.id.togglebutton_two_weeks)
            TimeFrame.MONTH -> timeFrameButtons.check(R.id.togglebutton_month)
            else -> return
        }
    }

    private fun initMembers(view: View) {
        navController = Navigation.findNavController(view)
        labelField = view.findViewById(R.id.editText_label)
        brandField = view.findViewById(R.id.editText_brand)
        amountField = view.findViewById(R.id.editText_amount)
        infoField = view.findViewById(R.id.editText_info)
        frequencyField = initFrequencyEditText(view)
        initImageView(view)
        unitDropdown = initUnitDropdown(view)
        timeFrameButtons = view.findViewById(R.id.freq_selection_togglegroup)
        val fab = view.findViewById<FloatingActionButton>(R.id.fab_new_fooditem)
        fab.setOnClickListener(this)
    }

    private fun initFrequencyEditText(view: View): EditText {
        val editText: EditText = view.findViewById(R.id.editText_freq)
        editText.setText(getString(R.string.freq_edittext_default))
        return editText
    }

    private fun initImageView(view: View) {
        imageView = view.findViewById(R.id.imageView_new_fooditem)
        imageView.setOnClickListener(this)
        if (imageUri != "") ImageViewPopulater.populateFromUri(
            requireContext(),
            imageUri,
            imageView
        )
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

    private fun getActiveToggleButton(): TimeFrame {
        return when (timeFrameButtons.checkedButtonId) {
            R.id.togglebutton_week -> TimeFrame.WEEK
            R.id.togglebutton_two_weeks -> TimeFrame.TWO_WEEKS
            R.id.togglebutton_month -> TimeFrame.MONTH
            else -> TimeFrame.NULL
        }
    }

    private fun getAmount(): Int {
        val amountString = amountField.text.toString()
        return if (amountString == "") AMOUNT_FIELD_EMPTY else amountString.toInt()
    }

    private fun getFrequency(): Int {
        val frequencyString = frequencyField.text.toString()
        return if (frequencyString == "") FREQUENCY_NOT_SET else frequencyString.toInt()
    }

    private fun foodItemCreationRequirementsMet(
        textFieldValues: MutableMap<String, String>,
        amount: Int,
        timeFrame: TimeFrame,
        frequency: Int,
        frequencyQuotient: Double
    ): Boolean {
        val checker = FoodItemCreationRequirementChecker(sharedPrefsHelper)
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
        ).setAnchorView(R.id.fab_new_fooditem).show()
    }

    /**
     * Called when any of the buttons in the fragment is clicked.
     */
    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.togglebutton_week -> timeFrameButtons.check(R.id.togglebutton_week)
            R.id.togglebutton_two_weeks -> timeFrameButtons.check(R.id.togglebutton_two_weeks)
            R.id.togglebutton_month -> timeFrameButtons.check(R.id.togglebutton_month)
            R.id.fab_new_fooditem -> {
                handleFabClick()
            }
            R.id.imageView_new_fooditem -> {
                handleImageClick()
            }
        }
    }

    private fun handleFabClick() {
        val label = labelField.text.toString()
        val brand = brandField.text.toString()
        val unit = unitDropdown.text.toString()
        val info = infoField.text.toString()
        val textFieldValues = mutableMapOf(
            "label" to label, "brand" to brand, "unit" to unit,
            "info" to info
        )
        val timeFrame = getActiveToggleButton()
        val amount = getAmount()
        val frequency = getFrequency()
        val groceryDaysAWeek = sharedPrefsHelper.getGroceryDays().size
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
            when (args.intention) {
                Intention.CREATE -> {
                    val foodItem = createFoodItemWithId(
                        ID_NOT_SET,
                        textFieldValues,
                        amount,
                        timeFrame,
                        frequency
                    )
                    foodItemViewModel.insert(foodItem)
                }
                Intention.EDIT -> {
                    val foodItem = createFoodItemWithId(
                        editedItem.id,
                        textFieldValues,
                        amount,
                        timeFrame,
                        frequency
                    )
                    foodItemViewModel.update(foodItem)
                }
                else -> return
            }
            activity?.onBackPressed()
        }
    }

    private fun createFoodItemWithId(
        id: Int,
        textFieldValues: MutableMap<String, String>,
        amount: Int,
        timeFrame: TimeFrame,
        frequency: Int,
    ): FoodItemEntity {
        return FoodItemEntity(
            id, imageUri, textFieldValues["label"]!!, textFieldValues["brand"]!!,
            textFieldValues["info"]!!, amount, textFieldValues["unit"]!!, timeFrame, frequency, 0.0
        )
    }

    private fun handleImageClick() {
        val cameraUtil = CameraUtil(requireContext())
        imageUri = cameraUtil.getImagePath()
        val toCaptureImage = cameraUtil.getIntentToCaptureImage()
        if (cameraUtil.cameraAppExists(toCaptureImage)) launchCameraApp(toCaptureImage)
    }

    private fun launchCameraApp(toCaptureImage: Intent) {
        startActivityForResult(toCaptureImage, RequestValidator.REQUEST_IMAGE_CAPTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (RequestValidator.imageCaptureSuccesful(requestCode, resultCode)) {
            ImageViewPopulater.populateFromUri(requireContext(), imageUri, imageView)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(IMAGE_PATH_KEY, imageUri)
    }
}