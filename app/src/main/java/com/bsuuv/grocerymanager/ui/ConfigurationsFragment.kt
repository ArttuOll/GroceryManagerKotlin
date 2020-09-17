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

class ConfigurationsFragment : Fragment() {

    private lateinit var mAdapter: ConfigurationsListAdapter
    private lateinit var mViewModel: FoodItemViewModel
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mRecyclerViewPlaceholder: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_configurations, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMembers(view)
        configureUi()
        setUpViewModel()
    }

    private fun initMembers(view: View) {
        mAdapter = ConfigurationsListAdapter(requireContext())
        mViewModel = ViewModelProvider(this).get(FoodItemViewModel::class.java)
        mRecyclerView = view.findViewById(R.id.config_recyclerview)
        mRecyclerViewPlaceholder = view.findViewById(R.id.config_recyclerview_placeholder)
    }

    private fun configureUi() {
        requireActivity().title = getString(R.string.activity_configs_title)
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        mRecyclerView.layoutManager = LinearLayoutManager(requireContext())
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
        mViewModel.getFoodItems().observe(requireActivity(), { foodItemEntities ->
            run {
                setRecyclerViewVisibility(foodItemEntities.size)
                mAdapter.setItems(foodItemEntities)
            }
        })
    }

    private fun setRecyclerViewVisibility(size: Int) {
        when {
            size > 0 -> RecyclerViewVisibilityToggle.toggle(
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
}