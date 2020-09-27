package com.bsuuv.grocerymanager.ui

import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bsuuv.grocerymanager.R
import com.bsuuv.grocerymanager.data.db.entity.FoodItemEntity
import com.bsuuv.grocerymanager.data.viewmodel.GroceryItemViewModel
import com.bsuuv.grocerymanager.ui.adapters.GroceryListAdapter
import com.bsuuv.grocerymanager.ui.util.RecyclerViewVisibilityToggle
import com.bsuuv.grocerymanager.util.DateTimeHelper
import com.bsuuv.grocerymanager.util.SharedPreferencesHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GroceryListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GroceryListAdapter
    private lateinit var recyclerViewPlaceholder: TextView
    private lateinit var viewModel: GroceryItemViewModel
    private lateinit var navController: NavController
    @Inject lateinit var dateTimeHelper: DateTimeHelper
    @Inject lateinit var sharedPrefsHelper: SharedPreferencesHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(GroceryItemViewModel::class.java)
        setUpViewModel()
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    private fun setUpViewModel() {
        if (dateTimeHelper.isGroceryDay()) {
            viewModel.getGroceryList().observe(viewLifecycleOwner, { adapter.setItems(it) })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navController = Navigation.findNavController(view)
        adapter = initAdapter(view)
        recyclerView = view.findViewById(R.id.main_recyclerview)
        recyclerViewPlaceholder = view.findViewById(R.id.main_recyclerview_placeholder)
        setUpRecyclerView()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initAdapter(view: View): GroceryListAdapter {
        val isWideScreen = view.findViewById<LinearLayout>(R.id.container_food_item_detail) != null
        return GroceryListAdapter(requireContext(), isWideScreen)
    }

    private fun setUpRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        setRecyclerViewVisibility()
        val itemTouchHelper = initializeItemTouchHelper()
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun setRecyclerViewVisibility() {
        val numberOfGroceryDays = sharedPrefsHelper.getGroceryDays().size
        when {
            numberOfGroceryDays == 0 -> {
                RecyclerViewVisibilityToggle.toggle(
                    recyclerView,
                    recyclerViewPlaceholder,
                    View.GONE,
                    R.string.main_no_grocery_days_set
                )
            }
            !dateTimeHelper.isGroceryDay() -> {
                RecyclerViewVisibilityToggle.toggle(
                    recyclerView,
                    recyclerViewPlaceholder,
                    View.GONE,
                    R.string.main_not_grocery_day
                )
            }
            dateTimeHelper.isGroceryDay() -> {
                RecyclerViewVisibilityToggle.toggle(
                    recyclerView,
                    recyclerViewPlaceholder,
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
                val deletedItem = adapter.getItemAtPosition(swipedPosition) as FoodItemEntity
                viewModel.deleteFromGroceryList(deletedItem)
                adapter.removeItemAtPosition(swipedPosition)
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_configure -> {
                navController.navigate(R.id.action_groceryListFragment_to_configsListFragment)
                true
            }
            R.id.action_settings -> {
                navController.navigate(R.id.action_groceryListFragment_to_settingsFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main, menu)
    }

    override fun onPause() {
        //Countdown values are incremented on grocery days. Calling updateItemCountdownValues()
        //also reset the list of items that have been incremented. Thus, if this was called on
        // grocery days, the countdown values would get incremented when navigating from grocery
        // list fragment. On non-grocery days GroceryListExtractor does nothing, so calling this
        // is safe.
        if (!dateTimeHelper.isGroceryDay()) viewModel.updateItemCountdownValues()
        super.onPause()
    }
}