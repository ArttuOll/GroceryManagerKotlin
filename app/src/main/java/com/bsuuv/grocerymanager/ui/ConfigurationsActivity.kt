package com.bsuuv.grocerymanager.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bsuuv.grocerymanager.R
import com.bsuuv.grocerymanager.data.db.entity.FoodItemEntity
import com.bsuuv.grocerymanager.data.viewmodel.FoodItemViewModel
import com.bsuuv.grocerymanager.ui.adapters.ConfigurationsListAdapter
import com.bsuuv.grocerymanager.ui.util.RecyclerViewVisibilityToggle
import com.bsuuv.grocerymanager.ui.util.RequestValidator
import com.bsuuv.grocerymanager.util.TimeFrame

/**
 * Activity for viewing all created food-items. Displays the items as a list or shows a placeholder
 * text if no food-items are yet created. The items can be swiped left or right to delete them, or
 * clicked to edit the. Contains a floating action button, which launches {@link
 * NewFoodItemActivity} for creating a new food-item.
 * <p>
 * The food-items are displayed in a <code>RecyclerView</code>, the {@link
 * ConfigurationsListAdapter} of which receives its data from a {@link FoodItemViewModel}.
 *
 * @see NewFoodItemActivity
 * @see ConfigurationsListAdapter
 * @see FoodItemViewModel
 */
class ConfigurationsActivity : AppCompatActivity() {

    private lateinit var mAdapter: ConfigurationsListAdapter
    private lateinit var mViewModel: FoodItemViewModel
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mRecyclerViewPlaceholder: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configurations)
        initMembers()
        configureUi()
        setUpViewModel()
    }

    private fun initMembers() {
        mAdapter = ConfigurationsListAdapter(this)
        mViewModel = ViewModelProvider(this).get(FoodItemViewModel::class.java)
        mRecyclerView = findViewById(R.id.config_recyclerview)
        mRecyclerViewPlaceholder = findViewById(R.id.config_recyclerview_placeholder)
    }

    private fun configureUi() {
        title = getString(R.string.activity_configs_title)
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = mAdapter
        val itemTouchHelper = initializeItemTouchHelper()
        itemTouchHelper.attachToRecyclerView(mRecyclerView)
    }

    private fun initializeItemTouchHelper(): ItemTouchHelper {
        return ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val deletedItemPosition = viewHolder.adapterPosition
                    val deletedItem =
                        mAdapter.getItemAtPosition(deletedItemPosition) as FoodItemEntity
                    mViewModel.delete(deletedItem)
                }
            })
    }

    private fun setUpViewModel() {
        mViewModel.getFoodItems().observe(this, { foodItemEntities ->
            run {
                setRecyclerViewVisibility(foodItemEntities.size)
                mAdapter.setItems(foodItemEntities)
            }
        })
    }

    private fun setRecyclerViewVisibility(size: Int) {
        when (size) {
            0 -> RecyclerViewVisibilityToggle.toggle(
                mRecyclerView,
                mRecyclerViewPlaceholder,
                View.VISIBLE,
                0
            )
            else -> RecyclerViewVisibilityToggle.toggle(
                mRecyclerView,
                mRecyclerViewPlaceholder,
                View.GONE,
                R.string.no_grocery_items
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, fromNewFoodItem: Intent?) {
        super.onActivityResult(requestCode, resultCode, fromNewFoodItem)
        if (RequestValidator.foodItemCreationSuccesful(requestCode, resultCode)) {
            val result = getFoodItemFromIntent()
            mViewModel.insert(result)
        } else if (RequestValidator.foodItemEditSuccesful(requestCode, resultCode)) {
            val id = intent?.getIntExtra("id", 0)!!
            val result = getFoodItemFromIntent(id = id)
            mViewModel.update(result)
        }
    }

    private fun getFoodItemFromIntent(id: Int = 0): FoodItemEntity {
        val label = intent.getStringExtra("label")!!
        val brand = intent.getStringExtra("brand")!!
        val amount = intent.getIntExtra("amount", 0)
        val unit = intent.getStringExtra("unit")!!
        val info = intent.getStringExtra("info")!!
        val timeFrame = intent.getSerializableExtra("time_frame")!!
        val frequency = intent.getIntExtra("frequency", 0)
        val imageUri = intent.getStringExtra("uri")!!
        val countdownValue = intent.getDoubleExtra("frequencyQuotient", 0.0)
        return FoodItemEntity(
            id,
            imageUri,
            label,
            brand,
            info,
            amount,
            unit,
            timeFrame as TimeFrame,
            frequency,
            countdownValue
        )
    }

    /**
     * Called when the floating action button in this activity is pressed. Launches
     * <code>NewFoodItemActivity</code> for creating a new <code>FoodItem</code>.
     *
     * @param view The view that has been clicked, in this case, the FAB. Default parameter required
     *             by the system.
     */
    fun onFabClick(view: View) {
        val toNewFoodItem = Intent(this, NewFoodItemActivity::class.java)
        val requestCode = RequestValidator.FOOD_ITEM_CREATE_REQUEST
        toNewFoodItem.putExtra("requestCode", requestCode)
        startActivityForResult(toNewFoodItem, requestCode)
    }
}