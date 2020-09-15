package com.bsuuv.grocerymanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
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
import javax.inject.Inject

class MainFragment : Fragment() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: GroceryListAdapter
    private lateinit var mRecyclerViewPlaceholder: TextView
    private lateinit var mViewModel: GroceryItemViewModel

    @Inject
    lateinit var mDateTimeHelper: DateTimeHelper

    @Inject
    lateinit var mSharedPrefsHelper: SharedPreferencesHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMembers(view)
        configureUi()
        setUpViewModel()
        scheduleNotification()
    }

    private fun initMembers(view: View) {
        mRecyclerView = view.findViewById(R.id.main_recyclerview)
        mAdapter = initAdapter(view)
        mRecyclerViewPlaceholder = view.findViewById(R.id.main_recyclerview_placeholder)
        mViewModel = ViewModelProvider(this).get(GroceryItemViewModel::class.java)
    }

    private fun initAdapter(view: View): GroceryListAdapter {
        val isWideScreen = view.findViewById<LinearLayout>(R.id.container_food_item_detail) != null
        return GroceryListAdapter(requireContext(), isWideScreen)
    }

    private fun configureUi() {
        setUpToolbar()
        setUpRecyclerView()
    }

    private fun setUpToolbar() {
        val toolbar = view?.findViewById<Toolbar>(R.id.toolbar)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).title = getToolbarTitle()
    }

    private fun getToolbarTitle(): String {
        return getString(R.string.mainActivity_actionbar_label) + " " + mDateTimeHelper.getCurrentDate()
    }

    private fun setUpRecyclerView() {
        mRecyclerView.layoutManager = LinearLayoutManager(requireContext())
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
            mViewModel.getGroceryList().observe(requireActivity(), { mAdapter.setItems(it) })
        }
    }

    private fun scheduleNotification() {
        val timeUntilGroceryDay = mDateTimeHelper.getTimeUntilNextGroceryDay()
        val notifier = GroceryDayNotifier(requireContext(), mSharedPrefsHelper, timeUntilGroceryDay)
        if (timeUntilGroceryDay < DateTimeHelper.NO_GROCERY_DAYS_SET) {
            notifier.scheduleGroceryDayNotification()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_configure -> {
                // TODO: fragment navigation
                true
            }
            R.id.action_settings -> {
                // TODO: fragment navigation
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}