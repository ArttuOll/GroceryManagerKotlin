package com.bsuuv.grocerymanager.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bsuuv.grocerymanager.R
import com.bsuuv.grocerymanager.data.db.entity.FoodItemEntity
import com.bsuuv.grocerymanager.data.viewmodel.FoodItemViewModel
import com.bsuuv.grocerymanager.ui.util.CameraUtil
import com.bsuuv.grocerymanager.ui.util.FoodItemCreationRequirementChecker
import com.bsuuv.grocerymanager.ui.util.ImageViewPopulater
import com.bsuuv.grocerymanager.ui.util.RequestValidator
import com.bsuuv.grocerymanager.util.SharedPreferencesHelper
import com.bsuuv.grocerymanager.util.TimeFrame
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


/**
 * Fragment for creating new one-time food-items. Displays a form with input fields corresponding to
 * different properties of a food-item:
 *
 * * An image, with a camera icon as a placeholder. Clicking the camera icon
 *     or an image launches the devices camera app, if one is installed.
 * * A label
 * * Brand
 * * Amount
 * * Unit for the amount
 * * Additional information the user wants to add
 *
 * The label field is mandatory.
 *
 * For information on validating the label field, see [FoodItemCreationRequirementChecker]
 *
 * Finally, the view contains a floating action button, which launches
 * validation of the input fields and afterwards sends their data to [ConfigsListFragment].
 *
 */
@AndroidEntryPoint
class NewOnetimeFoodItemFragment : Fragment(), View.OnClickListener {

    private object Keys {
        const val IMAGE_PATH_KEY = "imagePath"
        const val AMOUNT_FIELD_EMPTY = 0
        const val ID_NOT_SET = 0
    }

    private lateinit var labelField: EditText
    private lateinit var brandField: EditText
    private lateinit var amountField: EditText
    private lateinit var infoField: EditText
    private lateinit var imageView: ImageView
    private lateinit var unitDropdown: AutoCompleteTextView
    private lateinit var navController: NavController
    private val foodItemViewModel: FoodItemViewModel by viewModels()
    private var imageUri = ""
    @Inject lateinit var sharedPrefsHelper: SharedPreferencesHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (savedInstanceState != null) recoverFoodImage(savedInstanceState)
        return inflater.inflate(R.layout.fragment_new_onetime_fooditem, container, false)
    }

    private fun recoverFoodImage(savedInstanceState: Bundle) {
        imageUri = savedInstanceState.getString(Keys.IMAGE_PATH_KEY)!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initMembers(view)
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initMembers(view: View) {
        navController = Navigation.findNavController(view)
        labelField = view.findViewById(R.id.editText_label)
        brandField = view.findViewById(R.id.editText_brand)
        amountField = view.findViewById(R.id.editText_amount)
        infoField = view.findViewById(R.id.editText_info)
        initImageView(view)
        unitDropdown = initUnitDropdown(view)
        val fab = view.findViewById<FloatingActionButton>(R.id.fab_new_fooditem)
        fab.setOnClickListener(this)
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

    private fun getAmount(): Int {
        val amountString = amountField.text.toString()
        return if (amountString == "") Keys.AMOUNT_FIELD_EMPTY else amountString.toInt()
    }

    private fun foodItemCreationRequirementsMet(
        textFieldValues: MutableMap<String, String>,
        amount: Int
    ): Boolean {
        val checker = FoodItemCreationRequirementChecker(sharedPrefsHelper)
        return try {
            checker.requirementsMet(
                textFieldValues,
                amount,
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
        val amount = getAmount()

        if (foodItemCreationRequirementsMet(
                textFieldValues,
                amount,
            )
        ) {
            val foodItem = createOneTimeFoodItemWithId(textFieldValues, amount)
            foodItemViewModel.insert(foodItem)
            activity?.onBackPressed()
        }
    }

    private fun createOneTimeFoodItemWithId(
        textFieldValues: MutableMap<String, String>,
        amount: Int,
    ): FoodItemEntity {
        return FoodItemEntity(
            Keys.ID_NOT_SET, imageUri, textFieldValues["label"]!!, textFieldValues["brand"]!!,
            textFieldValues["info"]!!, amount, textFieldValues["unit"]!!, TimeFrame.WEEK, 1, 1.0,
            onetimeItem = true
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
        outState.putString(Keys.IMAGE_PATH_KEY, imageUri)
    }
}
