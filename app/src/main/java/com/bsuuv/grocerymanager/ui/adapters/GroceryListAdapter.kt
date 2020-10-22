package com.bsuuv.grocerymanager.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bsuuv.grocerymanager.R
import com.bsuuv.grocerymanager.data.model.FoodItem
import com.bsuuv.grocerymanager.ui.adapters.GroceryListAdapter.GroceryViewHolder
import com.bsuuv.grocerymanager.ui.util.ImageViewPopulater
import com.bsuuv.grocerymanager.ui.util.PluralsProvider

/**
 * Adapter that feeds grocery items in the form of [GroceryViewHolder]s to the
 * `RecyclerView` in [GroceryListFragment].
 */
class GroceryListAdapter(private val context: Context) : Adapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GroceryListAdapter.GroceryViewHolder {
        val itemView = inflater.inflate(R.layout.grocerylist_item, parent, false)
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
        RecyclerView.ViewHolder(itemView) {

        private val label: TextView = itemView.findViewById(R.id.grocery_item_label)
        private val brand: TextView = itemView.findViewById(R.id.grocery_item_brand)
        private val amount: TextView = itemView.findViewById(R.id.grocery_item_amount)
        private val pluralsProvider = PluralsProvider(context)
        private val imageView: ImageView = itemView.findViewById(R.id.grocerylist_food_image)
        private val layout: LinearLayout = itemView.findViewById(R.id.layout_grocery_list_item)

        init {
            imageView.clipToOutline = true
        }

        internal fun bindTo(currentItem: FoodItem) {
            setFieldsToValuesOf(currentItem)
            ImageViewPopulater.populateFromUri(context, currentItem.imageUri, imageView)
            layout.setOnClickListener {
                itemSelectedListener.onItemSelected(currentItem, imageView)
            }
        }

        private fun setFieldsToValuesOf(foodItem: FoodItem) {
            label.text = foodItem.label
            brand.text = foodItem.brand
            amount.text = pluralsProvider.getAmountString(foodItem.amount, foodItem.unit)
        }
    }

    interface ItemSelectedListener {
        fun onItemSelected(item: FoodItem, imageView: ImageView)
    }

    lateinit var itemSelectedListener: ItemSelectedListener
}