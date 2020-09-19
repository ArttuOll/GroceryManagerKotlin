package com.bsuuv.grocerymanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bsuuv.grocerymanager.R
import com.bsuuv.grocerymanager.data.db.entity.FoodItemEntity
import com.bsuuv.grocerymanager.data.viewmodel.FoodItemViewModel
import com.bsuuv.grocerymanager.ui.adapters.ConfigurationsListAdapter
import com.bsuuv.grocerymanager.ui.util.RecyclerViewVisibilityToggle

class ConfigsListFragment : Fragment() {

    private lateinit var adapter: ConfigurationsListAdapter
    private lateinit var viewModel: FoodItemViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewPlaceholder: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        adapter = ConfigurationsListAdapter(requireContext())
        viewModel = ViewModelProvider(this).get(FoodItemViewModel::class.java)
        return inflater.inflate(R.layout.activity_configurations, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initMembers(view)
        configureUi()
        setUpViewModel()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initMembers(view: View) {
        recyclerView = view.findViewById(R.id.config_recyclerview)
        recyclerViewPlaceholder = view.findViewById(R.id.config_recyclerview_placeholder)
    }

    private fun configureUi() {
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        val itemTouchHelper = initializeItemTouchHelper()
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun setUpViewModel() {
        viewModel.getFoodItems().observe(viewLifecycleOwner, { foodItemEntities ->
            run {
                setRecyclerViewVisibility(foodItemEntities.size)
                adapter.setItems(foodItemEntities)
            }
        })
    }

    private fun setRecyclerViewVisibility(size: Int) {
        when {
            size > 0 -> RecyclerViewVisibilityToggle.toggle(
                recyclerView,
                recyclerViewPlaceholder,
                View.VISIBLE,
                0
            )
            else -> RecyclerViewVisibilityToggle.toggle(
                recyclerView,
                recyclerViewPlaceholder,
                View.GONE,
                R.string.no_grocery_items
            )
        }
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
                        adapter.getItemAtPosition(deletedItemPosition) as FoodItemEntity
                    viewModel.delete(deletedItem)
                }
            })
    }
}