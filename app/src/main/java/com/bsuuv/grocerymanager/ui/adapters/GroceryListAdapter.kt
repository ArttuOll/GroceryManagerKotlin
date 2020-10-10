package com.bsuuv.grocerymanager.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bsuuv.grocerymanager.R
import com.bsuuv.grocerymanager.data.model.FoodItem
import com.bsuuv.grocerymanager.ui.GroceryItemDetailFragment
import com.bsuuv.grocerymanager.ui.adapters.GroceryListAdapter.GroceryViewHolder
import com.bsuuv.grocerymanager.ui.util.ImageViewPopulater
import com.bsuuv.grocerymanager.ui.util.PluralsProvider

/**
 * Adapter that feeds grocery items in the form of [GroceryViewHolder]s to the
 * `RecyclerView` in [GroceryListFragment].
 */
class GroceryListAdapter(
    private val mContext: Context,
    private val navController: NavController
) : Adapter() {

    private val mInflater: LayoutInflater = LayoutInflater.from(mContext)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GroceryListAdapter.GroceryViewHolder {
        val itemView = mInflater.inflate(R.layout.grocerylist_item, parent, false)
        return GroceryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentFoodItem = mItems[position]
        (holder as GroceryViewHolder).bindTo(currentFoodItem)
    }

    fun removeItemAtPosition(position: Int) {
        mItems.removeAt(position)
        notifyItemRemoved(position)
    }

    /**
     * A `ViewHolder` containing one grocery item to be displayed in the
     * `RecyclerView` in [GroceryListFragment].
     */
    inner class GroceryViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private val mLabel: TextView = itemView.findViewById(R.id.grocery_item_label)
        private val mBrand: TextView = itemView.findViewById(R.id.grocery_item_brand)
        private val mAmount: TextView = itemView.findViewById(R.id.grocery_item_amount)
        private val mPluralsProvider = PluralsProvider(mContext)
        private val mImage: ImageView = itemView.findViewById(R.id.grocerylist_food_image)

        init {
            itemView.setOnClickListener(this)
            mImage.clipToOutline = true
        }

        internal fun bindTo(currentItem: FoodItem) {
            setInputFieldValuesBasedOn(currentItem)
            ImageViewPopulater.populateFromUri(mContext, currentItem.imageUri, mImage)
        }

        private fun setInputFieldValuesBasedOn(foodItem: FoodItem) {
            mLabel.text = foodItem.label
            mBrand.text = foodItem.brand
            mAmount.text = mPluralsProvider.getAmountString(foodItem.amount, foodItem.unit)
        }

        /**
         * Called when an item in the `RecyclerView` in [GroceryListFragment] is clicked.
         * Checks if the device screen is wide (>900 dp) and based on that launches
         * [GroceryItemDetailFragment] in [GroceryListFragment].
         */
        //TODO: migrate to nav component
        override fun onClick(itemView: View?) {
            val currentFoodItem = mItems[adapterPosition]
            showInFoodItemDetailActivity(currentFoodItem)
        }

        private fun showInFoodItemDetailActivity(currentFoodItem: FoodItem) {
            val foodItemIdKey = GroceryItemDetailFragment.FOOD_ITEM_ID_KEY
            val args = bundleOf(foodItemIdKey to currentFoodItem.id)
            navController.navigate(
                R.id.action_groceryListFragment_to_groceryItemDetailFragment,
                args
            )
        }
    }

}