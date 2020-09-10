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
import androidx.recyclerview.widget.RecyclerView
import com.bsuuv.grocerymanager.R
import com.bsuuv.grocerymanager.data.model.FoodItem
import com.bsuuv.grocerymanager.ui.NewFoodItemActivity
import com.bsuuv.grocerymanager.ui.util.ImageViewPopulater
import com.bsuuv.grocerymanager.ui.util.PluralsProvider
import com.bsuuv.grocerymanager.ui.util.RequestValidator
import javax.inject.Inject

class ConfigurationsListAdapter(private val mContext: Context) :
    Adapter() {

    private val mInflater: LayoutInflater = LayoutInflater.from(mContext)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ConfigsViewHolder {
        val itemView = mInflater.inflate(R.layout.configlist_item, parent, false)
        return ConfigsViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val currentItem = mItems[position]
        (holder as ConfigsViewHolder).bindTo(currentItem)
    }

    inner class ConfigsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        private val mLabel: TextView = itemView.findViewById(R.id.config_item_label)
        private val mBrand: TextView = itemView.findViewById(R.id.config_item_brand)
        private val mAmount: TextView = itemView.findViewById(R.id.config_item_amount)
        private val mSchedule: TextView = itemView.findViewById(R.id.config_item_schedule)
        private val mImage: ImageView = itemView.findViewById(R.id.configlist_food_image)
        @Inject lateinit var mPlurals: PluralsProvider

        init {
            itemView.setOnClickListener(this)
            mImage.clipToOutline = true
        }

        internal fun bindTo(currentItem: FoodItem) {
            setInputFieldValuesBasedOn(currentItem)
        }

        private fun setInputFieldValuesBasedOn(currentItem: FoodItem) {
            mLabel.text = currentItem.label
            setBrandOrHideIfEmpty(currentItem.brand)
            mAmount.text = mPlurals.getAmountString(currentItem.amount, currentItem.unit)
            mSchedule.text = mPlurals.getScheduleString(
                currentItem.frequency,
                currentItem.timeFrame
            )
            ImageViewPopulater.populateFromUri(mContext, currentItem.imageUri, mImage)
        }

        private fun setBrandOrHideIfEmpty(brand: String) =
            if (brand == "") mBrand.visibility = View.GONE else mBrand.text = brand

        override fun onClick(v: View?) {
            val toNewFoodItem = createIntentToNewFoodItem()
            launchNewFoodItemActivity(toNewFoodItem)
        }

        private fun launchNewFoodItemActivity(toNewFoodItem: Intent) {
            val bundle = ActivityOptions.makeSceneTransitionAnimation(
                (mContext as Activity),
                mImage,
                mImage.transitionName
            ).toBundle()
            mContext.startActivityForResult(
                toNewFoodItem,
                RequestValidator.FOOD_ITEM_EDIT_REQUEST,
                bundle
            )
        }

        private fun createIntentToNewFoodItem(): Intent {
            val currentItem = mItems[adapterPosition]
            val toNewFoodItem = Intent(mContext, NewFoodItemActivity::class.java)
            toNewFoodItem.putExtra("label", currentItem.label)
            toNewFoodItem.putExtra("brand", currentItem.brand)
            toNewFoodItem.putExtra("info", currentItem.info)
            toNewFoodItem.putExtra("amount", currentItem.amount)
            toNewFoodItem.putExtra("unit", currentItem.unit)
            toNewFoodItem.putExtra("time_frame", currentItem.timeFrame)
            toNewFoodItem.putExtra("frequency", currentItem.frequency)
            toNewFoodItem.putExtra("id", currentItem.id)
            toNewFoodItem.putExtra("editPosition", adapterPosition)
            toNewFoodItem.putExtra("countdownValue", currentItem.countdownValue)
            toNewFoodItem.putExtra("requestCode", RequestValidator.FOOD_ITEM_EDIT_REQUEST)
            toNewFoodItem.putExtra("uri", currentItem.imageUri)
            return toNewFoodItem
        }
    }
}