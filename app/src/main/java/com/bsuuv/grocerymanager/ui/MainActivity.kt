package com.bsuuv.grocerymanager.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.bsuuv.grocerymanager.R
import com.bsuuv.grocerymanager.notifications.GroceryDayNotifier
import com.bsuuv.grocerymanager.util.DateTimeHelper
import com.bsuuv.grocerymanager.util.SharedPreferencesHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/*
* Main entry point of the app. This activity hosts all the fragments and sets the toolbar shared by
* them.
*
* When this activity is created, a notification is scheduled through [GroceryDayNotifier] to
* notify user on the grocery day.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject lateinit var dateTimeHelper: DateTimeHelper
    @Inject lateinit var sharedPrefsHelper: SharedPreferencesHelper
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.grocery_list_host)
        setUpToolbar()
        scheduleNotification()
    }

    private fun setUpToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        title = resources.getString(R.string.app_name)
        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment
        ) as NavHostFragment
        val navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun scheduleNotification() {
        val timeUntilGroceryDay = dateTimeHelper.getTimeUntilNextGroceryDay()
        val notifier = GroceryDayNotifier(this, sharedPrefsHelper, timeUntilGroceryDay)
        if (timeUntilGroceryDay < DateTimeHelper.NO_GROCERY_DAYS_SET) {
            notifier.scheduleGroceryDayNotification()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
