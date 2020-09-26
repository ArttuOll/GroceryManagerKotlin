package com.bsuuv.grocerymanager.ui

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.MultiSelectListPreference
import androidx.preference.PreferenceFragmentCompat
import com.bsuuv.grocerymanager.R
import com.bsuuv.grocerymanager.ui.util.WeekdaySorter
import java.util.*

/**
 * Fragment containing settings of the application. Hosts the setting `Preference`s in
 * `SettingsFragment` inner class.
 */
class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onResume() {
        super.onResume()
        setGroceryDaysSummary()
    }

    override fun onSharedPreferenceChanged(
        sharedPreferences: SharedPreferences?,
        key: String?
    ) {
        setGroceryDaysSummary()
    }

    private fun setGroceryDaysSummary() {
        val groceryDaysPreference = findPreference<MultiSelectListPreference>("grocerydays")
        val summary = getEntriesString(groceryDaysPreference)
        groceryDaysPreference?.summary = summary
    }

    private fun getEntriesString(preference: MultiSelectListPreference?): String {
        val entries = preference?.values as MutableSet<String>
        return buildEntriesString(entries)
    }

    private fun buildEntriesString(entries: MutableSet<String>?): String {
        if (entries == null) return ""
        val sortedEntries = WeekdaySorter.getAsSortedList(entries)
        val builder = StringBuilder()
        for ((index, entry) in sortedEntries.withIndex()) {
            if (index == sortedEntries.lastIndex) {
                builder.append(entry.capitalize(Locale.getDefault()))
            } else {
                builder.append(entry.capitalize(Locale.getDefault())).append(", ")
            }
        }
        return builder.toString()
    }
}
