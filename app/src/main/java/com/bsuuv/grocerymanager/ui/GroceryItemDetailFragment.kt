package com.bsuuv.grocerymanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bsuuv.grocerymanager.R
import com.bsuuv.grocerymanager.data.model.FoodItem
import com.bsuuv.grocerymanager.data.viewmodel.GroceryItemViewModel
import com.bsuuv.grocerymanager.ui.util.ImageViewPopulater
import com.bsuuv.grocerymanager.ui.util.PluralsProvider
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GroceryItemDetailFragment : Fragment() {

    companion object {
        const val FOOD_ITEM_ID_KEY = "foodItemId"

        fun newInstance(foodItemId: Int): GroceryItemDetailFragment {
            val fragment = GroceryItemDetailFragment()
            val args = Bundle()
            args.putInt(FOOD_ITEM_ID_KEY, foodItemId)
            fragment.arguments = args
            return fragment
        }
    }

    @Inject lateinit var mPluralsProvider: PluralsProvider
    private lateinit var mFoodItem: FoodItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initMembers()
    }

    private fun initMembers() {
        mFoodItem = getFoodItemFromArgs()
    }

    private fun getFoodItemFromArgs(): FoodItem {
        val viewModel = getViewModel()
        val fragmentArgs = requireArguments()
        val id = fragmentArgs.getInt(FOOD_ITEM_ID_KEY)
        return viewModel.get(id)
    }

    private fun getViewModel(): GroceryItemViewModel {
        val parentActivityOwner = requireActivity().application
        return GroceryItemViewModel(parentActivityOwner)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.food_item_detail, container, false)
        setUpImageViews(rootView)
        setUpTextViews(rootView)
        return rootView
    }

    private fun setUpImageViews(rootView: View?) {
        val foodImage: ImageView = rootView?.findViewById(R.id.imageView_detail)!!
        val uri = mFoodItem.imageUri
        context?.let { ImageViewPopulater.populateFromUri(it, uri, foodImage) }
    }

    private fun setUpTextViews(rootView: View?) {
        setUpLabelTextView(rootView)
        setUpAmountTextView(rootView)
        setUpBrandTextView(rootView)
        setUpInfoTextView(rootView)
    }

    private fun setUpLabelTextView(rootView: View?) {
        val labelView: TextView = rootView?.findViewById(R.id.textview_title)!!
        labelView.text = mFoodItem.label
    }

    private fun setUpAmountTextView(rootView: View?) {
        val amountView: TextView = rootView?.findViewById(R.id.textview_amount)!!
        val amount = mFoodItem.amount
        val unit = mFoodItem.unit
        amountView.text = (mPluralsProvider.getAmountString(amount, unit))
    }

    private fun setUpBrandTextView(rootView: View?) {
        val brandView: TextView = rootView?.findViewById(R.id.textview_brand)!!
        brandView.text = mFoodItem.brand
    }

    private fun setUpInfoTextView(rootView: View?) {
        val infoView: TextView = rootView?.findViewById(R.id.textview_info)!!
        infoView.text = mFoodItem.info
    }
}