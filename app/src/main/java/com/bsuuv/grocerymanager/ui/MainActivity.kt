package com.bsuuv.grocerymanager.ui

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import com.bsuuv.grocerymanager.R
import com.bsuuv.grocerymanager.data.viewmodel.GroceryItemViewModel
import com.bsuuv.grocerymanager.notifications.GroceryDayNotifier
import com.bsuuv.grocerymanager.ui.adapters.GroceryListAdapter
import dagger.hilt.android.AndroidEntryPoint

/**
 * The entry point and opening view of the app. Displays the grocery list or a placeholder when it's
 * not grocery day. The grocery items can be deleted by swiping them left or right. Contains an
 * options menu for navigating to [SettingsActivity] and [ConfigurationsActivity] and an
 * app bar that collapses when browsing the grocery-items. On wide-screen devices the layout of this
 * activity is split into two panes, the other one showing the grocery list, the other showing
 * details of a selected grocery item in [GroceryItemDetailFragment].
 *
 * The grocery list is displayed in a `RecyclerView`, the [GroceryListAdapter] of
 * which receives its data from a [GroceryItemViewModel].
 *
 * When this activity is created, a notification is scheduled through [GroceryDayNotifier] to
 * notify user on the grocery day.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

}