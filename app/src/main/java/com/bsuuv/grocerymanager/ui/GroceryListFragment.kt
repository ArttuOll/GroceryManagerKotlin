package com.bsuuv.grocerymanager.ui

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bsuuv.grocerymanager.R
import com.bsuuv.grocerymanager.data.db.entity.FoodItemEntity
import com.bsuuv.grocerymanager.data.model.FoodItem
import com.bsuuv.grocerymanager.data.viewmodel.GroceryItemViewModel
import com.bsuuv.grocerymanager.ui.adapters.AdapterItemSelectedListener
import com.bsuuv.grocerymanager.ui.adapters.GroceryListAdapter
import com.bsuuv.grocerymanager.ui.util.RecyclerViewVisibilityToggle
import com.bsuuv.grocerymanager.util.DateTimeHelper
import com.bsuuv.grocerymanager.util.SharedPreferencesHelper
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * The opening view of the app. Displays the grocery list or a placeholder when it's
 * not grocery day. The grocery items can be deleted by swiping them left or right. Contains an
 * options menu for navigating to [SettingsFragment] and [ConfigsListFragment] and an
 * app bar that collapses when browsing the grocery-items.
 *
 * The grocery list is displayed in a `RecyclerView`, the [GroceryListAdapter] of
 * which receives its data from a [GroceryItemViewModel].
 *
 */
@AndroidEntryPoint
class GroceryListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GroceryListAdapter
    private lateinit var recyclerViewPlaceholder: TextView
    private lateinit var navController: NavController
    private lateinit var fab: ExtendedFloatingActionButton
    private val viewModel: GroceryItemViewModel by viewModels()
    @Inject lateinit var dateTimeHelper: DateTimeHelper
    @Inject lateinit var sharedPrefsHelper: SharedPreferencesHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        adapter = getConfiguredAdapter()
        recyclerView = view.findViewById(R.id.main_recyclerview)
        recyclerViewPlaceholder = view.findViewById(R.id.main_recyclerview_placeholder)
        setUpFab(view)
        setUpRecyclerView()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun getConfiguredAdapter(): GroceryListAdapter {
        adapter = GroceryListAdapter(requireContext())
        adapter.itemSelectedListener = object : AdapterItemSelectedListener {
            override fun onItemSelected(item: FoodItem, imageView: ImageView) {
                val extras = FragmentNavigatorExtras(imageView to item.imageUri)
                val action = GroceryListFragmentDirections
                    .actionGroceryListFragmentToGroceryItemDetailFragment(
                        itemId = item.id,
                        imageUri = item.imageUri
                    )
                navController.navigate(action, extras)
            }
        }
        return adapter
    }

    private fun setUpFab(view: View) {
        this.fab = view.findViewById(R.id.grocerylist_fab)
        fab.setOnClickListener {
            navController.navigate(
                GroceryListFragmentDirections
                    .actionGroceryListFragmentToNewOnetimeFoodItemFragment()
            )
        }
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
                RecyclerViewVisibilityToggle.toggleOff(
                    recyclerView,
                    recyclerViewPlaceholder,
                    R.string.main_no_grocery_days_set,
                    fab
                )
            }
            !dateTimeHelper.isGroceryDay() -> {
                RecyclerViewVisibilityToggle.toggleOff(
                    recyclerView,
                    recyclerViewPlaceholder,
                    R.string.main_not_grocery_day,
                    fab
                )
            }
            dateTimeHelper.isGroceryDay() -> {
                RecyclerViewVisibilityToggle.toggleOn(
                    recyclerView,
                    recyclerViewPlaceholder,
                    fab
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
                navController.navigate(
                    GroceryListFragmentDirections
                        .actionGroceryListFragmentToConfigsListFragment()
                )
                true
            }
            R.id.action_settings -> {
                navController.navigate(
                    GroceryListFragmentDirections
                        .actionGroceryListFragmentToSettingsFragment()
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main, menu)
    }

    override fun onPause() {
        /*
         Countdown values are incremented and one-time items are deleted on grocery days.If this
         was called on grocery days, the countdown values would get incremented when
         navigating from grocery list fragment. On non-grocery days GroceryListExtractor
         does nothing, so calling this is safe
         */
        if (!dateTimeHelper.isGroceryDay()) viewModel.onGroceryDayPassed()
        super.onPause()
    }
}