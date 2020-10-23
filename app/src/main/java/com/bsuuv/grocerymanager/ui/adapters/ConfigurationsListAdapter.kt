package com.bsuuv.grocerymanager.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bsuuv.grocerymanager.R
import com.bsuuv.grocerymanager.data.model.FoodItem
import com.bsuuv.grocerymanager.ui.adapters.ConfigurationsListAdapter.ConfigsViewHolder
import com.bsuuv.grocerymanager.ui.util.ImageViewPopulater
import com.bsuuv.grocerymanager.ui.util.Intention
import com.bsuuv.grocerymanager.ui.util.PluralsProvider

/**
 * Adapter that feeds food-items in the form of [ConfigsViewHolder]s to the
 * `RecyclerView` in [ConfigurationsActivity].
 */
class ConfigurationsListAdapter(
    private val context: Context,
    private val navController: NavController
) :
    Adapter() {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    lateinit var itemSelectedListener: AdapterItemSelectedListener

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

        private val label: TextView = itemView.findViewById(R.id.config_item_label)
        private val brand: TextView = itemView.findViewById(R.id.config_item_brand)
        private val amount: TextView = itemView.findViewById(R.id.config_item_amount)
        private val schedule: TextView = itemView.findViewById(R.id.config_item_schedule)
        private val imageView: ImageView = itemView.findViewById(R.id.configlist_food_image)
        private val plurals: PluralsProvider = PluralsProvider(context)
        private val layout: LinearLayout = itemView.findViewById(R.id.config_item_linearlayout)

        init {
            itemView.setOnClickListener(this)
            imageView.clipToOutline = true
        }

        internal fun bindTo(currentItem: FoodItem) {
            setInputFieldValuesBasedOn(currentItem)
            layout.setOnClickListener {
                itemSelectedListener.onItemSelected(currentItem, imageView)
            }
        }

        private fun setInputFieldValuesBasedOn(currentItem: FoodItem) {
            label.text = currentItem.label
            setBrandOrHideIfEmpty(currentItem.brand)
            amount.text = plurals.getAmountString(currentItem.amount, currentItem.unit)
            schedule.text = plurals.getScheduleString(
                currentItem.frequency,
                currentItem.timeFrame
            )
            ImageViewPopulater.populateFromUri(context, currentItem.imageUri, imageView)
        }

        private fun setBrandOrHideIfEmpty(brand: String) =
            if (brand == "") this.brand.visibility = View.GONE else this.brand.text = brand

        override fun onClick(v: View?) {
            val currentItemId = mItems[adapterPosition].id
            val args = bundleOf("intention" to Intention.EDIT, "editedItemId" to currentItemId)
            navController.navigate(R.id.action_configsListFragment_to_newFoodItemFragment, args)
        }
    }
}