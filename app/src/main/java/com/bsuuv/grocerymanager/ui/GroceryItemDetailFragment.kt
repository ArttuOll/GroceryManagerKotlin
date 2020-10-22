package com.bsuuv.grocerymanager.ui

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bsuuv.grocerymanager.R
import com.bsuuv.grocerymanager.data.model.FoodItem
import com.bsuuv.grocerymanager.data.viewmodel.GroceryItemViewModel
import com.bsuuv.grocerymanager.ui.util.ImageViewPopulater
import com.bsuuv.grocerymanager.ui.util.PluralsProvider
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Fragment for viewing the details of a grocery-item.
 */
@AndroidEntryPoint
class GroceryItemDetailFragment : Fragment() {

    @Inject lateinit var mPluralsProvider: PluralsProvider
    private lateinit var mFoodItem: FoodItem
    private val viewModel: GroceryItemViewModel by viewModels()
    private val args: GroceryItemDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        initMembers()
    }

    private fun initMembers() {
        mFoodItem = viewModel.get(args.itemId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_item_detail, container, false)
        setUpImageView(rootView)
        setUpTextViews(rootView)
        return rootView
    }

    private fun setUpImageView(rootView: View?) {
        val foodImage: ImageView = rootView?.findViewById(R.id.imageView_detail)!!
        foodImage.apply {
            transitionName = getString(R.string.transition_detail)
            ImageViewPopulater.populateFromUri(context, args.imageUri, this)
        }
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