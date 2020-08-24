package com.bsuuv.grocerymanager.util

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.bsuuv.grocerymanager.data.model.FoodItem
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

/**
 * An utility class to handle all interactions with <code>SharedPreferences</code>.
 */
class SharedPreferencesHelper(context: Context) {

    val sharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)
    private val gson: Gson

    companion object {
        const val GROCERY_DAYS_KEY = "grocerydays"
    }

    init {
        val builder = GsonBuilder()
        this.gson = builder.create()
    }

    /**
     * Returns a list of grocery days selected by the user in SettingsActivity.
     *
     * @return A <code>Set</code> containing the grocery days as lowercase strings. If none are
     * selected, an empty <code>Set</code> is returned.
     */
    fun getGroceryDays(): MutableSet<String> = sharedPreferences
        .getStringSet(GROCERY_DAYS_KEY, HashSet())!!

    fun saveList(foodItems: MutableList<FoodItem>, key: String) {
        val json = gson.toJson(foodItems)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(key, json)
        editor.apply()
    }

    fun getList(key: String): MutableList<FoodItem> {
        val json = sharedPreferences.getString(key, "")
        return if (json == "") ArrayList() else gson.fromJson<MutableList<FoodItem>>(json)
    }

    fun clearList(key: String) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.remove(key)
        editor.apply()
    }

    private inline fun <reified T> Gson.fromJson(json: String?) =
        fromJson<T>(json, object : TypeToken<T>() {}.type)
}
