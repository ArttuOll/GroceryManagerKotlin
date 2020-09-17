package com.bsuuv.grocerymanager.ui

import android.os.Bundle
import android.view.*
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
    @Inject lateinit var dateTimeHelper: DateTimeHelper
    @Inject lateinit var sharedPrefsHelper: SharedPreferencesHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(GroceryItemViewModel::class.java)
        setUpViewModel()
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    private fun setUpViewModel() {
        if (dateTimeHelper.isGroceryDay()) {
            viewModel.getGroceryList().observe(viewLifecycleOwner, { adapter.setItems(it) })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = initAdapter(view)
        recyclerView = view.findViewById(R.id.main_recyclerview)
        recyclerViewPlaceholder = view.findViewById(R.id.main_recyclerview_placeholder)
        setUpToolbar(view)
        setUpRecyclerView()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initAdapter(view: View): GroceryListAdapter {
        val isWideScreen = view.findViewById<LinearLayout>(R.id.container_food_item_detail) != null
        return GroceryListAdapter(requireContext(), isWideScreen)
    }

    private fun setUpToolbar(view: View) {
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        requireActivity().title = getToolbarTitle()
    }

    private fun getToolbarTitle(): String {
        return getString(R.string.mainActivity_actionbar_label) + " " + dateTimeHelper.getCurrentDate()
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main, menu)
    }
}