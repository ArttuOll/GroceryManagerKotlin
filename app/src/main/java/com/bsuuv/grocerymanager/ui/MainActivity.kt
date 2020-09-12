package com.bsuuv.grocerymanager.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bsuuv.grocerymanager.R
import com.bsuuv.grocerymanager.data.db.entity.FoodItemEntity
import com.bsuuv.grocerymanager.data.viewmodel.GroceryItemViewModel
import com.bsuuv.grocerymanager.notifications.GroceryDayNotifier
import com.bsuuv.grocerymanager.ui.adapters.GroceryListAdapter
import com.bsuuv.grocerymanager.ui.util.RecyclerViewVisibilityToggle
import com.bsuuv.grocerymanager.util.DateTimeHelper
import com.bsuuv.grocerymanager.util.SharedPreferencesHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * The entry point and opening view of the app. Displays the grocery list or a placeholder when it's
 * not grocery day. The grocery items can be deleted by swiping them left or right. Contains an
 * options menu for navigating to [SettingsActivity] and [ConfigurationsActivity] and an
 * app bar that collapses when browsing the grocery-items. On wide-screen devices the layout of this
 * activity is split into two panes, the other one showing the grocery list, the other showing
 * details of a selected grocery item in [GroceryItemDetailFragment].
 * <p>
 * The grocery list is displayed in a <code>RecyclerView</code>, the [GroceryListAdapter] of
 * which receives its data from a [GroceryItemViewModel].
 * <p>
 * When this activity is created, a notification is scheduled through [GroceryDayNotifier] to
 * notify user on the grocery day.
 *
 * @see SettingsActivity
 * @see ConfigurationsActivity
 * @see GroceryItemDetailFragment
 * @see GroceryListAdapter
 * @see GroceryItemViewModel
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: GroceryListAdapter
    private lateinit var mRecyclerViewPlaceholder: TextView
    private lateinit var mViewModel: GroceryItemViewModel
    @Inject lateinit var mDateTimeHelper: DateTimeHelper
    @Inject lateinit var mSharedPrefsHelper: SharedPreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initMembers()
        configureUi()
        setUpViewModel()
        scheduleNotification()
    }

    private fun initMembers() {
        mRecyclerView = findViewById(R.id.main_recyclerview)
        mAdapter = initAdapter()
        mRecyclerViewPlaceholder = findViewById(R.id.main_recyclerview_placeholder)
        mViewModel = ViewModelProvider(this).get(GroceryItemViewModel::class.java)
    }

    private fun initAdapter(): GroceryListAdapter {
        val isWideScreen = findViewById<LinearLayout>(R.id.container_food_item_detail) != null
        return GroceryListAdapter(this, isWideScreen)
    }

    private fun configureUi() {
        setUpToolbar()
        setUpRecyclerView()
    }

    private fun setUpToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        title = getToolbarTitle()
    }

    private fun getToolbarTitle(): String {
        return getString(R.string.mainActivity_actionbar_label) + " " + mDateTimeHelper.getCurrentDate()
    }

    private fun setUpRecyclerView() {
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = mAdapter
        setRecyclerViewVisibility()
        val itemTouchHelper = initializeItemTouchHelper()
        itemTouchHelper.attachToRecyclerView(mRecyclerView)
    }

    private fun setRecyclerViewVisibility() {
        val numberOfGroceryDays = mSharedPrefsHelper.getGroceryDays().size
        when {
            numberOfGroceryDays == 0 -> {
                RecyclerViewVisibilityToggle.toggle(
                    mRecyclerView,
                    mRecyclerViewPlaceholder,
                    View.GONE,
                    R.string.main_no_grocery_days_set
                )
            }
            !mDateTimeHelper.isGroceryDay() -> {
                RecyclerViewVisibilityToggle.toggle(
                    mRecyclerView,
                    mRecyclerViewPlaceholder,
                    View.GONE,
                    R.string.main_not_grocery_day
                )
            }
            mDateTimeHelper.isGroceryDay() -> {
                RecyclerViewVisibilityToggle.toggle(
                    mRecyclerView,
                    mRecyclerViewPlaceholder,
                    View.VISIBLE,
                    0
                )
            }
        }
    }

    private fun initializeItemTouchHelper(): ItemTouchHelper {
        return ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val swipedPosition = viewHolder.adapterPosition
                val deletedItem = mAdapter.getItemAtPosition(swipedPosition) as FoodItemEntity
                mViewModel.deleteFromGroceryList(deletedItem)
                mAdapter.removeItemAtPosition(swipedPosition)
            }
        })
    }

    private fun setUpViewModel() {
        if (mDateTimeHelper.isGroceryDay()) {
            mViewModel.getGroceryList().observe(this, { mAdapter.setItems(it) })
        }
    }

    private fun scheduleNotification() {
        val timeUntilGroceryDay = mDateTimeHelper.getTimeUntilNextGroceryDay()
        val notifier = GroceryDayNotifier(this, mSharedPrefsHelper, timeUntilGroceryDay)
        if (timeUntilGroceryDay < DateTimeHelper.NO_GROCERY_DAYS_SET) {
            notifier.scheduleGroceryDayNotification()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_configure -> {
                launchConfigurationsActivity()
                true
            }
            R.id.action_settings -> {
                launchSettingsActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun launchConfigurationsActivity() {
        val toConfigurationsActivity = Intent(this, ConfigurationsActivity::class.java)
        startActivity(toConfigurationsActivity)
    }

    private fun launchSettingsActivity() {
        val toSettingsActivity = Intent(this, SettingsActivity::class.java)
        startActivity(toSettingsActivity)
    }
}