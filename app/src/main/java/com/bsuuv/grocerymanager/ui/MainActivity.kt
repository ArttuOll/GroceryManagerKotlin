package com.bsuuv.grocerymanager.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.bsuuv.grocerymanager.R
import com.bsuuv.grocerymanager.data.viewmodel.GroceryItemViewModel
import com.bsuuv.grocerymanager.notifications.GroceryDayNotifier
import com.bsuuv.grocerymanager.ui.adapters.GroceryListAdapter
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
 *
 * The grocery list is displayed in a <code>RecyclerView</code>, the [GroceryListAdapter] of
 * which receives its data from a [GroceryItemViewModel].
 *
 * When this activity is created, a notification is scheduled through [GroceryDayNotifier] to
 * notify user on the grocery day.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject lateinit var mDateTimeHelper: DateTimeHelper
    @Inject lateinit var mSharedPrefsHelper: SharedPreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.grocery_list_host)

        if (savedInstanceState == null) {
            val fragment = GroceryListFragment()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.main_content, fragment)
                .commit()
        }
        scheduleNotification()
    }

    private fun scheduleNotification() {
        val timeUntilGroceryDay = mDateTimeHelper.getTimeUntilNextGroceryDay()
        val notifier = GroceryDayNotifier(this, mSharedPrefsHelper, timeUntilGroceryDay)
        if (timeUntilGroceryDay < DateTimeHelper.NO_GROCERY_DAYS_SET) {
            notifier.scheduleGroceryDayNotification()
        }
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