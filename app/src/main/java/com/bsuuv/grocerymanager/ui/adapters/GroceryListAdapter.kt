package com.bsuuv.grocerymanager.ui.adapters

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bsuuv.grocerymanager.R
import com.bsuuv.grocerymanager.data.model.FoodItem
import com.bsuuv.grocerymanager.ui.GroceryItemDetailActivity
import com.bsuuv.grocerymanager.ui.GroceryItemDetailFragment
import com.bsuuv.grocerymanager.ui.util.ImageViewPopulater
import com.bsuuv.grocerymanager.ui.util.PluralsProvider

class GroceryListAdapter(private val mContext: Context, private val mIsWideScreen: Boolean) :
    Adapter() {

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
     * A <code>ViewHolder</code> containing one grocery item to be displayed in the
     * <code>RecyclerView</code> in [MainActivity].
     *
     * @see MainActivity
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
         * Called when an item in the <code>RecyclerView</code> in {@link MainActivity} is clicked.
         * Checks if the device screen is wide (>900 dp) and based on that launches {@link
         * GroceryItemDetailFragment} either in {@link MainActivity} or {@link
         * GroceryItemDetailActivity}.
         *
         * @param v Default parameter from the parent method. The <code>View</code> that was clicked.
         * @see MainActivity
         * @see GroceryItemDetailFragment
         * @see GroceryItemDetailActivity
         */
        override fun onClick(itemView: View?) {
            val currentFoodItem = mItems[adapterPosition]
            if (mIsWideScreen) showInMainActivity(currentFoodItem)
            else showInFoodItemDetailActivity(currentFoodItem)
        }

        private fun showInMainActivity(currentFoodItem: FoodItem) {
            val fragment = GroceryItemDetailFragment.newInstance(currentFoodItem.id)
            (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.container_food_item_detail, fragment)
                .addToBackStack("")
                .commit()
        }

        private fun showInFoodItemDetailActivity(currentFoodItem: FoodItem) {
            val toFoodItemDetail = createIntentToFoodItemDetail(currentFoodItem)
            val bundle = ActivityOptions.makeSceneTransitionAnimation(
                (mContext as Activity),
                mImage,
                mImage.transitionName
            ).toBundle()
            mContext.startActivity(toFoodItemDetail, bundle)
        }

        private fun createIntentToFoodItemDetail(foodItem: FoodItem): Intent {
            val toFoodItemDetail = Intent(mContext, GroceryItemDetailActivity::class.java)
            toFoodItemDetail.putExtra(GroceryItemDetailFragment.FOOD_ITEM_ID_KEY, foodItem.id)
            return toFoodItemDetail
        }

    }

}